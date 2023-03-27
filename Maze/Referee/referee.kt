package Referee

import Common.*
import Common.board.Board
import Common.board.Coordinates
import Common.player.PlayerData
import Common.tile.GameTile
import Players.PlayerMechanism
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * A Maze game referee entity. To handle player's when running a game to completion.
 *
 * The following steps are done when starting a game:
 *  1. For every player (in the given order of ascending age), get a board suggestion.
 *  2. Chose a board and create the initial game state.
 *  3. In the same order as above, transmit the initial public state and treasure goal to every player.
 *  4. Play a game.
 *     - Until 1000 rounds pass, or a player wins, or every player passes, get the current player move.
 *     - If the move request terminates with no raised exceptions and the move supplied is valid
 *       according to the rules of the game, then  apply the move to the state, move on the next player,
 *       otherwise kick out the player from the game and terminate all communication
 *     - Once the game is over, determine a winner:
 *       * A player who found its home after having found the treasure wins.
 *       * If no single player accomplished this, the players who share the smallest euclidian distance to home after
 *         having found the treasure are all tied winners.
 *       * If no player found the treasure, the players who share the smallest euclidian distance to the treasure tile
 *         are all winner.
 *  5. For every player with active communication, send its win/loss information.
 *
 *  The TCP server component must deal with a player exceeding the alotted time to respond to a request, if this happens
 *  the player must be removed.
 */
abstract class Referee {
    
    protected val WIDTH = 7
    protected val HEIGHT= 7

    private val observers = mutableListOf<ObserverMechanism>()

    /**
     * Selects a suggested board and creates a game from it.
     */
    protected abstract fun createStateFromChosenBoard(suggestedBoards: List<Board>, players: List<PlayerMechanism>): GameState

    /**
     * Begins a game given a list of players (ordered by player age). Sets initial game data to all players,
     * plays one full game, and sends winning player data to players.
     */
    fun startGame(players: List<PlayerMechanism>, goals: Queue<Coordinates>): EndgameData {

        val (gameState, playersThatSuggestedBoard, misbehavedOnPropose) = setup(players)

        val (playersThatResponded, misbehavedOnSetup) = sendInitialGameStateData(gameState, playersThatSuggestedBoard)

        val (winners, playersThatCheated) = playGame(gameState, playersThatResponded, goals)

        val allCheaters = misbehavedOnPropose.map{ it.name }.toList() + misbehavedOnSetup.map{ it.name }.toList() + playersThatCheated

        return sendGameOverInformation(winners, players, allCheaters)
    }


    /**
     * Get board suggests from all players, creates a game with the chosen one.
     */
    private fun setup(players: List<PlayerMechanism>): Triple<GameState, List<PlayerMechanism>, List<PlayerMechanism>> {
        val (suggestedBoards, playersInGame, playersNotInGame) = queryAllPlayers(players) {
            it.proposeBoard0(WIDTH, HEIGHT)
        }
        val gameState = createStateFromChosenBoard(
            validateBoards(suggestedBoards.values.toList(), WIDTH, HEIGHT), playersInGame
        )
        return Triple(gameState, playersInGame, playersNotInGame)
    }

    /**
     * Transmits initial game data to all players, including the public game state and the player's goal.
     */
    private fun sendInitialGameStateData(gameState: GameState, players: List<PlayerMechanism>):
            Pair<List<PlayerMechanism>, List<PlayerMechanism>> {
        val initialPlayerState = gameState.toPublicState()
        val (_, playersThatResponded, playersThatDidNot) = queryAllPlayers(players) { player ->
            val goal = gameState.getPlayerGoal(player.name)
            player.setupAndUpdateGoal(initialPlayerState, goal)
        }
        return Pair(playersThatResponded, playersThatDidNot)
    }


    /**
     * Runs a single game of maze to completion.
     */
    fun playGame(
        initialState: GameState, players: List<PlayerMechanism>, additionalGoals: Queue<Coordinates>
    ): Pair<List<String>, List<String>> {
        val playerMechanisms = players.associateBy { it.name }
        var state = initialState
        val cheaters = mutableSetOf<PlayerMechanism>()
        while (!state.isGameOver()) {
            val currentPlayer = state.getActivePlayer()

            state = playerMechanisms[currentPlayer.id]?.let { playerMechanism ->
                runPlayerTurnSafely(currentPlayer, playerMechanism, state, cheaters, additionalGoals)
            } ?: state.kickOutActivePlayer()

            notifyObservers(state)
        }

        observers.forEach{
            it.gameOver()
        }

        val winnerNames = getWinners(state)
        return Pair(winnerNames, cheaters.map { it.name }.toList())
    }

    private fun notifyObservers(state: GameState) {
        observers.forEach{ it.updateState(state) }
    }

    fun registerObserver(observerMechanism: ObserverMechanism) {
        observers.add(observerMechanism)
    }


    /**
     * Transmits endgame data, a single win/loss value.
     */
    private fun sendGameOverInformation(winners: List<String>, players: List<PlayerMechanism>, cheaters: List<String>): EndgameData {
        val (newWinningData, _, didNotRespond) = queryAllPlayers(players) { player ->
            winners.contains(player.name).let { playerWon -> player.won(playerWon) ; playerWon }
        }
        val realWinners = newWinningData.filter { it.value }.map {
            it.key
        }.sorted()

        val responseNameSet = didNotRespond.map { it.name }.toSet()
        val allcheaters = (cheaters.toSet() + responseNameSet).toList().sorted()
        val losers = players.filter {
            (!winners.contains(it.name) && !cheaters.contains(it.name))
        }.map { it.name }.sorted()
        return EndgameData(realWinners, losers, allcheaters)
    }

    /**
     * Performs an action on every player sequentially. If the action correctly returns a value, it notes it.
     * If the player misbehaves, the player is not included in the returned mechanisms so it never gets used again.
     */
    private fun <T> queryAllPlayers(players: List<PlayerMechanism>, action: (PlayerMechanism) -> T):
            Triple<Map<String, T>, List<PlayerMechanism>, List<PlayerMechanism>> {
        val answers = mutableMapOf<String, T>()
        val playersThatResponded = mutableListOf<PlayerMechanism>()
        val playersThatDidNot = mutableListOf<PlayerMechanism>()
        for (player in players) {
            val playerAnswer = safelyQueryPlayer(player, action)
            playerAnswer?.let {
                answers[player.name] = it ; playersThatResponded.add(player)
            } ?: run { playersThatDidNot.add(player) }
        }
        return Triple(answers, playersThatResponded, playersThatDidNot)
    }

    private fun <T> safelyQueryPlayer(player: PlayerMechanism, action: (PlayerMechanism) -> T): T? {
        val executor = Executors.newSingleThreadExecutor()
        val future = executor.submit(Callable { action(player) })

        val result = try {
            future.get(TIMEOUT_SECOND, TimeUnit.SECONDS)
        } catch (e: Exception) {
            future.cancel(true)
            null
        }
        executor.shutdown()
        return result
    }

    /**
     * Runs a single round. If the player API call throws an exception, the player
     * will be removed from the game.
     */
    protected open fun runPlayerTurnSafely(
        currentPlayer: PlayerData,
        currentMechanism: PlayerMechanism,
        state: GameState,
        cheaters: MutableSet<PlayerMechanism>,
        additionalGoals: Queue<Coordinates>
    ): GameState {
        return safelyQueryPlayer(currentMechanism) {
            playOnePlayerTurn(currentPlayer, currentMechanism, state, cheaters, additionalGoals)
        } ?: kickoutActivePlayer(state, currentMechanism, cheaters)
    }


    /**
     * Plays an entire round with the currentPlayer. Receives a move from players, if it is valid it executes it,
     * otherwise kick out the player. If the player reached the treasure, it will send it its home.
     */
    private fun playOnePlayerTurn(
        currentPlayer: PlayerData,
        currentMechanism: PlayerMechanism,
        state: GameState,
        cheaters: MutableSet<PlayerMechanism>,
        additionalGoals: Queue<Coordinates>
    ): GameState {
        val suggestedMove = currentMechanism.takeTurn(state.toPublicState())
        if (isMoveValid(suggestedMove, state)) {
            var newState = performMove(suggestedMove, state)
            if (newState.hasActivePlayerReachedTreasure() && !state.hasActivePlayerReachedTreasure()) {
                if (additionalGoals.isEmpty()) {
                    newState = newState.updateCurrentPlayerGoal(
                        newGoal = currentPlayer.homePosition,
                        foundTreasure = true,
                        isLastGoal = false
                    )
                    currentMechanism.setupAndUpdateGoal(null, currentPlayer.homePosition)
                } else {
                    val newGoal = additionalGoals.poll()
                    newState = newState.updateCurrentPlayerGoal(
                        newGoal = newGoal,
                        foundTreasure = false,
                        isLastGoal = additionalGoals.isEmpty()
                    )
                    currentMechanism.setupAndUpdateGoal(newState.toPublicState(), newGoal)
                }
            }
            return newState.endActivePlayerTurn(suggestedMove == Skip)
        }
        return kickoutActivePlayer(state, currentMechanism, cheaters)
    }

    private fun kickoutActivePlayer(state: GameState, playerMechanism: PlayerMechanism, cheaters: MutableSet<PlayerMechanism>): GameState {
        cheaters.add(playerMechanism)
        return state.kickOutActivePlayer()
    }


    private fun getWinners(state: GameState): List<String> {

        val players = state.getPlayersData().map { it.value }
            .sortedByDescending { it.numberOfTreasuresReached }
        if (players.isEmpty()) return listOf()

        // Max treasure
        val maxGoalsReached = players[0].numberOfTreasuresReached
        val playersWithHighestTreasures = choosePlayerWithMaxTreasure(players, maxGoalsReached)

        val endingPlayer = state.playerThatFoundItsHome
        // If the player who returned home has the max treasure, wins automatically
        if (endingPlayer != null && playersWithHighestTreasures.contains(endingPlayer)) {
            return listOf(endingPlayer.id)
        }

        return findPlayersWhoWereClosestToGoal(playersWithHighestTreasures)
    }

    private fun choosePlayerWithMaxTreasure(players: List<PlayerData>, maxGoalsReached: Int): List<PlayerData> {
        val playersWithHighestTreasures = mutableListOf<PlayerData>()
        for (player in players) {
            if (player.numberOfTreasuresReached == maxGoalsReached) {
                playersWithHighestTreasures.add(player)
            } else {
                break
            }
        }
        return playersWithHighestTreasures
    }

    /**
     * Returns the players that were closest to their respective treasure.
     */
    private fun findPlayersWhoWereClosestToGoal(players: Collection<PlayerData>): List<String> {
        val minDistance = minimumDistanceToPosition(players) { it.goalPosition }
        val result = mutableListOf<String>()
        players.forEach {
            if (it.currentPosition.euclidDistanceTo(it.goalPosition).equalsDelta(minDistance)) {
                result.add(it.id)
            }
        }
        return result
    }

    private fun minimumDistanceToPosition(playersData: Collection<PlayerData>, getPos: (PlayerData) -> Coordinates): Double {
        return playersData.minOfOrNull { getPos(it).euclidDistanceTo(it.currentPosition) }!!
    }

    /**
     * Returns if the move is valid based on the current state.
     */
    private fun isMoveValid(action: Action, state: GameState): Boolean {
        return when(action) {
            is Skip -> true
            is RowAction -> state.isValidRowMove(action.rowPosition, action.direction, action.rotation, action.newPosition)
            is ColumnAction -> state.isValidColumnMove(action.columnPosition, action.direction, action.rotation, action.newPosition)
        }
    }

    /**
     * Perform the move on behalf of player and returns the resulting GameState.
     */
    private fun performMove(action: Action, state: GameState): GameState {
        return when(action) {
            is Skip -> state
            is RowAction -> state.slideRowAndInsertSpare(action.rowPosition, action.direction, action.rotation, action.newPosition)
            is ColumnAction -> state.slideColumnAndInsertSpare(action.columnPosition, action.direction, action.rotation, action.newPosition)
        }
    }

    /**
     * Given a list of 2d arrays of tiles, returns the boards that are valid.
     */
    private fun validateBoards(suggestedBoardTiles: List<Array<Array<GameTile>>>, width: Int, height: Int): List<Board> {
        val validBoards = mutableListOf<Board>()
        for (suggestedTiles in suggestedBoardTiles) {
            if (Board.tilesAreValid(suggestedTiles, width, height)) {
                validBoards.add(Board(suggestedTiles))
            }
        }
        return validBoards
    }

    companion object {
        private const val TIMEOUT_SECOND = 4L
        private const val DELTA = 0.000001
        fun Double.equalsDelta(other: Double) = abs(this - other) < DELTA // equality check for Doubles using DELTA
    }
}
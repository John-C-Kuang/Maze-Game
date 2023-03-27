package Players

import Common.GameState
import Common.TestData
import Common.board.Board
import Common.board.Coordinates
import Common.player.BaseColor
import Common.player.PlayerData
import Common.tile.Degree
import Common.tile.GameTile
import Common.tile.Path
import Common.tile.treasure.Gem
import Common.tile.treasure.Treasure
import Players.SamplePlayerMechanisms.*
import Referee.Referee
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.util.*
import kotlin.test.assertEquals

internal class RefereeTest {

    val player1 = TestData.createPlayer1()
    val player2 = TestData.createPlayer2()
    val player3 = TestData.createPlayer3()
    val state = TestData.createGameStateWithPlayers(player1, player2, player3)
    val initialPublicState = state.toPublicState()

    val referee = TestTestableReferee()


    @Test
    fun testSendProposeBoard() {
        val willAlwaysPassPlayers = listOf(
            PassingPlayerMechanism("player1"),
            PassingPlayerMechanism("player2"),
            PassingPlayerMechanism("player3")
        )

        referee.startGame(willAlwaysPassPlayers)

        assertEquals(
            listOf(initialPublicState, initialPublicState, initialPublicState),
            willAlwaysPassPlayers.map { it.receivedState })
    }

    @Test
    fun testPlayersWhoThrowExceptionDuringBoardRequestNotIncluded() {
        val playerMechanisms = listOf(
            PassingPlayerMechanism("player1"),
            MisbehavingOnBoardRequest("player2"),
            PassingPlayerMechanism("player3")
        )

        referee.startGame(playerMechanisms)

        val expectedState = TestData.createGameStateWithPlayers(player1, player3).toPublicState()
        // second player never received message
        assertEquals(
            listOf(expectedState, null, expectedState),
            playerMechanisms.map { it.receivedState }
        )
    }

    @Test
    fun testPlayersWhoThrowExceptionDuringInitialStateTransmissionDoNotPlay() {
        val playerMechanisms = listOf(
            PassingPlayerMechanism("player1"),
            PassingPlayerMechanism("player2"),
            MisbehavingOnSetup("player3")
        )

        referee.startGame(playerMechanisms)

        val expectedState =
            TestData.createGameStateWithPlayers(player1, player2, player3).toPublicState()

        assertEquals(
            listOf(expectedState, expectedState, null),
            playerMechanisms.map { it.receivedState }
        )
    }

    @Test
    fun testPlayersWhoThrowExceptionDuringGameIsKickedOut() {
        val playerMechanism = listOf(
            MisbehavingOnRound("player1"),
            PassingPlayerMechanism("player2"),
            PassingPlayerMechanism("player3")
        )

        assertDoesNotThrow { referee.startGame(playerMechanism) }
    }

    @Test
    fun testPlayer1WinsEasily() {
        val riemannPlayers = createRiemannPlayers()
        val winabbleBoard = TestData.createBoard(TestData.easyToWinBoard)
        val gameState = GameState(
            winabbleBoard,
            TestData.createSpareTile(),
            listOf(player1, player2, player3),
        )

        val (winningData, cheaters) = referee.playGame(gameState, riemannPlayers, LinkedList())

        assertEquals(
            mapOf("player1" to true, "player2" to false, "player3" to false),
            winningData
        )
        assertEquals(
            setOf(),
            cheaters
        )
    }

    @Test
    fun testPlayersAllPassNoOneFoundTreasure() {
        val stuckPlayers = listOf(
            PassingPlayerMechanism("player1"),
            PassingPlayerMechanism("player2"),
            PassingPlayerMechanism("player3")
        )
        val impossibleBoard = TestData.createBoard(TestData.impossibleBoard)

        val gameState = GameState(
            impossibleBoard, GameTile(
                Path.UP_RIGHT, Degree.NINETY,
                Treasure(Gem.GROSSULAR_GARNET, Gem.GOLDSTONE)
            ),
            createStuckPlayersClosestToTreasure()
        )

        val (endgameData, cheaters) = referee.playGame(gameState, stuckPlayers, LinkedList())

        assertEquals(
            mapOf("player1" to true, "player2" to false, "player3" to false),
            endgameData
        )

        assertEquals(
            setOf(),
            cheaters
        )
    }

    @Test
    fun testEveryPlayerGetsKickedOut() {
        val badPlayers = listOf(
            MisbehavingOnRound("player1"),
            MisbehavingOnRound("player2"),
            MisbehavingOnRound("player3")
        )

        val (noWinners, cheaters) = referee.playGame(state, badPlayers, LinkedList())

        assertEquals(mapOf(), noWinners)

        assertEquals(badPlayers.toSet(), cheaters)
    }

    @Test
    fun testOnePlayerGetsKickedOut() {
        val players = listOf(
            RandomBoardRiemannPlayerMechanism("player1", player1.getGoal()),
            MisbehavingOnRound("player2"),
            PassingPlayerMechanism("player3")
        )

        val easyBoard = TestData.createBoard(TestData.easyToWinBoard)
        val state = GameState(
            easyBoard,
            TestData.createSpareTile(),
            listOf(player1, player2, player3)
        )

        val (endgame, cheaters) = referee.playGame(state, players, LinkedList())

        assertEquals(
            mapOf("player1" to true, "player3" to false),
            endgame
        )
        assertEquals(
            setOf("player2"),
            cheaters.map { it.name }.toSet()
        )
    }

    @Test
    fun testAllPlayersPassWinnerFoundTreasureTied() {
        val stuckPlayers = listOf(
            PassingPlayerMechanism("player1"),
            PassingPlayerMechanism("player2"),
            PassingPlayerMechanism("player3")
        )
        val impossibleBoard = TestData.createBoard(TestData.impossibleBoard)

        val gameState = GameState(
            impossibleBoard, GameTile(
                Path.UP_RIGHT, Degree.NINETY,
                Treasure(Gem.GROSSULAR_GARNET, Gem.GOLDSTONE)
            ),
            createStuckPlayersClosestToHome()
        )

        val (endgameData, _) = referee.playGame(gameState, stuckPlayers, LinkedList())

        assertEquals(
            mapOf("player3" to true, "player2" to true, "player1" to false),
            endgameData
        )
    }

    @Test
    fun testGracefulWhenPlayerMisbehavesOnWon() {
        val players = listOf(
            PassingPlayerMechanism("player1"),
            MisbehavingOnWon("player2"),
            MisbehavingOnWon("player3")
        )

        assertDoesNotThrow { referee.startGame(players) }
    }

    @Test
    fun playerTakesTooLongIsRemoved() {
        val players = listOf(
            PassingPlayerMechanism("player1"),
            PlayerDoesNotReturn("player2"),
            PassingPlayerMechanism("player3")
        )

        val endgame = referee.startGame(players)

        assert(endgame.cheaters.contains("player2"))

    }

    /**
     * Bug: Closest distance to any goal is player1 to its treasure, but player 1 has found treasure and
     * is on its way back home. Player 2 should win
     */
    @Test
    fun testGetWinnersClosestToHomeButDidNotFindTreasure() {
        val players = listOf(
            PlayerData(
                "player1",
                Coordinates.fromRowAndValue(0, 1),
                Coordinates.fromRowAndValue(1, 1),
                Coordinates.fromRowAndValue(6, 6),
                BaseColor.BLACK,
                numberOfTreasuresReached = 0
            ),
            PlayerData(
                "player2",
                Coordinates.fromRowAndValue(3, 3),
                Coordinates.fromRowAndValue(0, 6),
                Coordinates.fromRowAndValue(5, 5),
                BaseColor.RED,
                hasReachedGoal = true,
                numberOfTreasuresReached = 1
            )
        )
        val mechanisms = listOf(
            PassingPlayerMechanism("player1"), PassingPlayerMechanism("player2")
        )

        val state = TestData.createGameStateWithPlayers(players)

        val endgame = referee.playGame(state, mechanisms, LinkedList())

        assertEquals(
            mapOf("player1" to false, "player2" to true),
            endgame.first
        )
    }

    fun createRiemannPlayers(): List<RandomBoardRiemannPlayerMechanism> {
        return listOf(
            RandomBoardRiemannPlayerMechanism("player1", player1.getGoal()),
            RandomBoardRiemannPlayerMechanism("player2", player2.getGoal()),
            RandomBoardRiemannPlayerMechanism("player3", player3.getGoal()),
        )
    }

    private fun createStuckPlayersClosestToTreasure(): List<PlayerData> {
        return listOf(
            PlayerData(
                "player1",
                Coordinates.fromRowAndValue(1, 1),
                Coordinates.fromRowAndValue(3, 3),
                Coordinates.fromRowAndValue(1, 1),
                BaseColor.BLACK,
                numberOfTreasuresReached = 0
            ),
            PlayerData(
                "player2",
                Coordinates.fromRowAndValue(1, 5),
                Coordinates.fromRowAndValue(1, 1),
                Coordinates.fromRowAndValue(1, 5),
                BaseColor.PURPLE,
                numberOfTreasuresReached = 0
            ),
            PlayerData(
                "player3",
                Coordinates.fromRowAndValue(5, 5),
                Coordinates.fromRowAndValue(1, 1),
                Coordinates.fromRowAndValue(5, 5),
                BaseColor.RED,
                numberOfTreasuresReached = 0
            )
        )
    }

    private fun createStuckPlayersClosestToHome(): List<PlayerData> {
        return listOf(
            PlayerData(
                "player3",
                Coordinates.fromRowAndValue(1, 1),
                Coordinates.fromRowAndValue(3, 3),
                Coordinates.fromRowAndValue(3, 3),
                BaseColor.BLACK,
                hasReachedGoal = true,
                numberOfTreasuresReached = 1
            ),
            PlayerData(
                "player1",
                Coordinates.fromRowAndValue(1, 5),
                Coordinates.fromRowAndValue(1, 5),
                Coordinates.fromRowAndValue(1, 5),
                BaseColor.PURPLE,
                numberOfTreasuresReached = 0
            ),
            PlayerData(
                "player2",
                Coordinates.fromRowAndValue(3, 3),
                Coordinates.fromRowAndValue(1, 1),
                Coordinates.fromRowAndValue(1, 1),
                BaseColor.RED,
                hasReachedGoal = true,
                numberOfTreasuresReached = 1
            )
        )
    }

    @Test
    fun testPlayGameOnePlayerWithMultipleGoals() {
        val riemannPlayers = createRiemannPlayers()
        val winabbleBoard = TestData.createBoard(TestData.easyToWinBoard)
        val gameState = GameState(
            winabbleBoard,
            TestData.createSpareTile(),
            listOf(player1),
        )

        val goals = LinkedList<Coordinates>()
        goals.add(Coordinates.fromRowAndValue(1, 1))
        goals.add(Coordinates.fromRowAndValue(3, 3))
        goals.add(Coordinates.fromRowAndValue(5, 5))
        goals.add(Coordinates.fromRowAndValue(1, 3))

        val (winningData, cheaters) = referee.playGame(gameState, riemannPlayers, goals)

        assertEquals(
            mapOf("player1" to true),
            winningData
        )

        assertEquals(
            setOf(),
            cheaters
        )
    }

    @Test
    fun testMultiplePlayerWithOneGoal() {
        val riemannPlayers = createRiemannPlayers()
        val winabbleBoard = TestData.createBoard(TestData.easyToWinBoard)
        val gameState = GameState(
            winabbleBoard,
            TestData.createSpareTile(),
            listOf(player1, player2, player3),
        )

        val goals = LinkedList<Coordinates>()
        goals.add(Coordinates.fromRowAndValue(1, 1))

        val (winningData, cheaters) = referee.playGame(gameState, riemannPlayers, goals)

        assertEquals(
            mapOf("player1" to false, "player2" to true, "player3" to false),
            winningData
        )

        assertEquals(
            setOf(),
            cheaters
        )
    }

    @Test
    fun testMultiplePlayerWithMultipleGoals() {
        val riemannPlayers = createRiemannPlayers()
        val winabbleBoard = TestData.createBoard(TestData.easyToWinBoard)
        val gameState = GameState(
            winabbleBoard,
            TestData.createSpareTile(),
            listOf(player1, player2, player3),
        )

        val goals = LinkedList<Coordinates>()
        goals.add(Coordinates.fromRowAndValue(1, 1))
        goals.add(Coordinates.fromRowAndValue(3, 3))
        goals.add(Coordinates.fromRowAndValue(5, 5))
        goals.add(Coordinates.fromRowAndValue(1, 3))

        val (winningData, cheaters) = referee.playGame(gameState, riemannPlayers, goals)

        assertEquals(
            mapOf("player1" to false, "player2" to true, "player3" to false),
            winningData
        )

        assertEquals(
            setOf(),
            cheaters
        )
    }


    @Test
    fun testPlayerGoalNumberOnePlayerWithNoGoals() {
        val riemannPlayers = createRiemannPlayers()
        val winabbleBoard = TestData.createBoard(TestData.easyToWinBoard)
        val gameState = GameState(
            winabbleBoard,
            TestData.createSpareTile(),
            listOf(player1),
        )

        val goals = LinkedList<Coordinates>()

        val returnState = referee.playGameWithAReturnedState(gameState, riemannPlayers, goals)

        assertEquals(2, returnState.getActivePlayer().numberOfTreasuresReached)
    }

    @Test
    fun testPlayerGoalNumberOnePlayerWithMultipleGoals() {
        val riemannPlayers = createRiemannPlayers()
        val winabbleBoard = TestData.createBoard(TestData.easyToWinBoard)
        val gameState = GameState(
            winabbleBoard,
            TestData.createSpareTile(),
            listOf(player1),
        )

        val goals = LinkedList<Coordinates>()
        goals.add(Coordinates.fromRowAndValue(1, 1))
        goals.add(Coordinates.fromRowAndValue(3, 3))
        goals.add(Coordinates.fromRowAndValue(5, 5))
        goals.add(Coordinates.fromRowAndValue(1, 3))

        val returnState = referee.playGameWithAReturnedState(gameState, riemannPlayers, goals)

        assertEquals(3, returnState.getActivePlayer().numberOfTreasuresReached)
    }

    @Test
    fun testPlayerGoalNumberMultiplePlayerWithOneGoal() {
        val riemannPlayers = createRiemannPlayers()
        val winabbleBoard = TestData.createBoard(TestData.easyToWinBoard)
        val gameState = GameState(
            winabbleBoard,
            TestData.createSpareTile(),
            listOf(player1, player2, player3),
        )

        val goals = LinkedList<Coordinates>()
        goals.add(Coordinates.fromRowAndValue(1, 1))

        val returnState = referee.playGameWithAReturnedState(gameState, riemannPlayers, goals)

        val players = returnState.getPlayersData()

        players["player1"]?.let { assertEquals(1, it.numberOfTreasuresReached) }
        players["player2"]?.let { assertEquals(2, it.numberOfTreasuresReached) }
        players["player3"]?.let { assertEquals(0, it.numberOfTreasuresReached) }
    }

    @Test
    fun testPlayerGoalNumberMultiplePlayerWithMultipleGoals() {
        val riemannPlayers = createRiemannPlayers()
        val winabbleBoard = TestData.createBoard(TestData.easyToWinBoard)
        val gameState = GameState(
            winabbleBoard,
            TestData.createSpareTile(),
            listOf(player1, player2, player3),
        )

        val goals = LinkedList<Coordinates>()
        goals.add(Coordinates.fromRowAndValue(1, 1))
        goals.add(Coordinates.fromRowAndValue(3, 3))
        goals.add(Coordinates.fromRowAndValue(5, 5))
        goals.add(Coordinates.fromRowAndValue(1, 3))

        val returnState = referee.playGameWithAReturnedState(gameState, riemannPlayers, goals)

        val players = returnState.getPlayersData()

        players["player1"]?.let { assertEquals(2, it.numberOfTreasuresReached) }
        players["player2"]?.let { assertEquals(4, it.numberOfTreasuresReached) }
        players["player3"]?.let { assertEquals(0, it.numberOfTreasuresReached) }
    }
}

class TestTestableReferee : Referee() {
    override fun createStateFromChosenBoard(
        suggestedBoards: List<Board>,
        players: List<PlayerMechanism>
    ): GameState {
        val testPlayers =
            listOf(TestData.createPlayer1(), TestData.createPlayer2(), TestData.createPlayer3())
                .associateBy { it.id }
        val playerData = players.map {
            testPlayers[it.name] ?: throw IllegalStateException("Need test data for $it.name")
        }

        return TestData.createGameStateWithPlayers(playerData)
    }

    fun playGameWithAReturnedState(
        initialState: GameState, players: List<PlayerMechanism>, additionalGoals: Queue<Coordinates>
    ): GameState {
        val playerMechanisms = players.associateBy { it.name }
        var state = initialState
        val cheaters = mutableSetOf<PlayerMechanism>()
        while (!state.isGameOver()) {
            val currentPlayer = state.getActivePlayer()

            state = playerMechanisms[currentPlayer.id]?.let { playerMechanism ->
                runPlayerTurnSafely(currentPlayer, playerMechanism, state, cheaters, additionalGoals)
            } ?: state.kickOutActivePlayer()

        }

        return state
    }

//    fun getPlayers():  {
//
//    }
}



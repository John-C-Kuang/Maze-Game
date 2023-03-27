package Players

import Common.Action
import Common.PublicGameState
import Common.board.Coordinates
import Common.player.PlayerData
import Common.tile.GameTile
import testing.StrategyDesignation

open class StrategyPlayerMechanism(
    override val name: String,
    private val strategy: StrategyDesignation
): PlayerMechanism {

    private lateinit var nextGoal: Coordinates
    private var hasFoundTreasure = false
    var wonMessage: Boolean? = null

    override fun proposeBoard0(rows: Int, columns: Int): Array<Array<GameTile>> {
        return arrayOf()
    }

    override fun setupAndUpdateGoal(state: PublicGameState?, goal: Coordinates) {
        if (state == null) {
            this.hasFoundTreasure = true
        }
        this.nextGoal = goal
    }


    override fun takeTurn(state: PublicGameState): Action {
        val (name, currentPosition, homePosition, color, numberOfTreasuresReached) = state.getActivePlayerData()
        val playerData = PlayerData(name, currentPosition, nextGoal, homePosition, color, hasFoundTreasure, numberOfTreasuresReached)
        return strategy.getStrategy(playerData).decideMove(state)
    }

    override fun won(hasPlayerWon: Boolean) {
        wonMessage = hasPlayerWon
    }
}
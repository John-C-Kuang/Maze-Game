package Players.SamplePlayerMechanisms

import Common.Action
import Common.PublicGameState
import Common.Skip
import Common.TestData
import Common.board.Coordinates
import Common.tile.GameTile
import Players.PlayerMechanism

open class PassingPlayerMechanism(override val name: String): PlayerMechanism {
    var receivedState: PublicGameState? = null
    var hasWon = false
    override fun proposeBoard0(rows: Int, columns: Int): Array<Array<GameTile>> {
        return TestData.createTiles()
    }

    override fun setupAndUpdateGoal(state: PublicGameState?, goal: Coordinates) {
        receivedState = state
    }

    override fun takeTurn(state: PublicGameState): Action {
        return Skip
    }

    override fun won(hasPlayerWon: Boolean) {
        hasWon = hasPlayerWon
    }
}
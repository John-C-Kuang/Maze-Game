package Players.SamplePlayerMechanisms

import Common.Action
import Common.PublicGameState
import Common.Skip
import Common.board.Coordinates
import Common.tile.GameTile

class MisbehavingOnBoardRequest(override val name: String): PassingPlayerMechanism(name) {
    override fun proposeBoard0(rows: Int, columns: Int): Array<Array<GameTile>> {
        throw IllegalStateException("I misbehave when asked to get a board :devil:")
    }
}

class MisbehavingOnSetup(override val name: String): PassingPlayerMechanism(name) {
    override fun setupAndUpdateGoal(state: PublicGameState?, goal: Coordinates) {
        throw IllegalStateException("NOOO!!!")
    }

    override fun won(hasPlayerWon: Boolean) {
        throw IllegalStateException("This should not be raised, would mean won was called after this player misbehaved.")
    }
}

class MisbehavingOnRound(override val name: String): PassingPlayerMechanism(name) {
    override fun takeTurn(state: PublicGameState): Action {
        throw IllegalStateException("Bad move baby")
    }

    override fun won(hasPlayerWon: Boolean) {
        throw IllegalStateException("This should not be raised, would mean won was called after this player misbehaved.")
    }
}

class MisbehavingOnWon(override val name: String): PassingPlayerMechanism(name) {
    override fun won(hasPlayerWon: Boolean) {
        throw IllegalStateException("MEAN")
    }
}

class PlayerDoesNotReturn(override val name: String): PassingPlayerMechanism(name) {
    override fun takeTurn(state: PublicGameState): Action {
        do {
            Skip
        } while(true)
    }
}
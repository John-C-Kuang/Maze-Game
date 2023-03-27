package testing

import Common.Action
import Common.PublicGameState
import Common.Skip
import Common.board.Coordinates
import Players.StrategyPlayerMechanism


class MisbehavingOnSetup(
    override val name: String,
    strategy: StrategyDesignation,
    ): StrategyPlayerMechanism(name, strategy) {
    override fun setupAndUpdateGoal(state: PublicGameState?, goal: Coordinates) {
        1 / 0
    }
}

class MisbehavingOnRound(
    override val name: String,
    strategy: StrategyDesignation,
): StrategyPlayerMechanism(name, strategy) {
    override fun takeTurn(state: PublicGameState): Action {
        1 / 0
        return Skip
    }
}

class MisbehavingOnWon(
    override val name: String,
    strategy: StrategyDesignation,
): StrategyPlayerMechanism(name, strategy) {
    override fun won(hasPlayerWon: Boolean) {
        1 / 0
    }
}

abstract class MisbehavingPlayersWithDelay(
    override val name: String,
    strategy: StrategyDesignation,
    initialCount: Int
): StrategyPlayerMechanism(name, strategy) {
    private var count = initialCount

    protected fun misbehaveOnCount() {
        if (count == 1) {
            while (true) {}
        }
        count -= 1
    }
}
class MisbehavingOnSetupDelay(
    override val name: String,
    strategy: StrategyDesignation,
    count: Int
): MisbehavingPlayersWithDelay(name, strategy, count) {
    override fun setupAndUpdateGoal(state: PublicGameState?, goal: Coordinates) {
        misbehaveOnCount()
        super.setupAndUpdateGoal(state, goal)
    }
}

class MisbehavingOnTakeTurnDelay(
    override val name: String,
    strategy: StrategyDesignation,
    count: Int
): MisbehavingPlayersWithDelay(name, strategy, count) {
    override fun takeTurn(state: PublicGameState): Action {
        misbehaveOnCount()
        return super.takeTurn(state)
    }
}

class MisbehavingOnWonDelay(
    override val name: String,
    strategy: StrategyDesignation,
    count: Int
): MisbehavingPlayersWithDelay(name, strategy, count) {
    override fun won(hasPlayerWon: Boolean) {
        misbehaveOnCount()
        super.won(hasPlayerWon)
    }
}



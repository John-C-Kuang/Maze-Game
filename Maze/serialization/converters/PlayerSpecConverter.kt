package serialization.converters

import Players.StrategyPlayerMechanism
import testing.*

object PlayerSpecConverter {

    fun getPlayerMechanism(playerSpec: List<Any>): StrategyPlayerMechanism {
        val name = playerSpec[0].toString()
        val strategy = StrategyDesignation.valueOf(playerSpec[1].toString())
        return when (playerSpec.size) {
            2 -> StrategyPlayerMechanism(name,strategy)
            3 -> badPlayerMechanism(name, strategy, playerSpec[2].toString())
            4 -> badPlayerMechanismCount(name, strategy, playerSpec[2].toString(),
                (playerSpec[3] as Double).toInt())
            else -> throw IllegalArgumentException("Invalid player spec, size is: ${playerSpec.size}")
        }
    }

    private fun badPlayerMechanism(name: String, strategy: StrategyDesignation,misb: String): StrategyPlayerMechanism {
        return when(misb) {
            "setUp" -> MisbehavingOnSetup(name, strategy)
            "takeTurn" -> MisbehavingOnRound(name, strategy)
            "win" -> MisbehavingOnWon(name, strategy)
            else -> throw IllegalArgumentException("Expected a BadFM, found $misb")
        }
    }

    private fun badPlayerMechanismCount(
        name: String,
        strategy: StrategyDesignation,
        misb: String,
        count: Int
    ): StrategyPlayerMechanism {
        return when(misb) {
            "setUp" -> MisbehavingOnSetupDelay(name, strategy, count)
            "takeTurn" -> MisbehavingOnTakeTurnDelay(name, strategy, count)
            "win" -> MisbehavingOnWonDelay(name, strategy, count)
            else -> throw IllegalArgumentException("Expected a BadFM, found: $misb")
        }
    }
}
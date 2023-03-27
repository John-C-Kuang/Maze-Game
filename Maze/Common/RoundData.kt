package Common

import Common.player.PlayerData

/**
 * Represents the data stored in one round of game.
 */
data class RoundData(
    // how many rounds have passed
    val roundCount: Int,

    // how many turns have passed in the current round, starts at 0
    // is at most `numberOfPlayersInRound - 1`
    val turnsInRound: Int,

    // stays the same throughout the entire round, even after players get removed
    val numberOfPlayersInRound: Int,
    val allTurnsWereSkip: Boolean,
    val lastRoundWasSkip: Boolean
) {

    fun incrementTurnCount(playersInNewRound: Int, isPlayerPass: Boolean): RoundData {
        val newTurn = turnsInRound + 1
        return if (newTurn == numberOfPlayersInRound) {
            newRound(playersInNewRound, isPlayerPass)
        }
        else {
            this.copy(
                turnsInRound = newTurn,
                allTurnsWereSkip = allTurnsWereSkip && isPlayerPass)
        }
    }

    private fun newRound(playersInNewRound: Int, isPlayerPass: Boolean): RoundData {
        return this.copy(
            roundCount = roundCount + 1,
            turnsInRound = 0,
            numberOfPlayersInRound = playersInNewRound,
            allTurnsWereSkip = true,
            lastRoundWasSkip = allTurnsWereSkip && isPlayerPass
        )
    }

    companion object {
        fun init(players: List<PlayerData>): RoundData {
            return RoundData(0, 0, players.size, true, false)
        }
    }

}
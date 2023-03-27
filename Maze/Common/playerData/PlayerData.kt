package Common.player

import Common.board.Coordinates

/**
 * Represents a Maze player, every player has a treasure goal, position and color for home tile.
 */
data class PlayerData(
    val id: String,
    val currentPosition: Coordinates,
    val goalPosition: Coordinates,
    val homePosition: Coordinates,
    val color: Color,
    val hasReachedGoal: Boolean = false,
    val numberOfTreasuresReached: Int,
    val pursuingLastGoal: Boolean = false
) {

    fun getGoal(): Coordinates {
        return if (hasReachedGoal) homePosition else goalPosition
    }

    override fun equals(other: Any?): Boolean {
        return other is PlayerData && other.id == this.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun toPublicPlayerData(): PublicPlayerData {
        return PublicPlayerData(id, currentPosition, homePosition, color, numberOfTreasuresReached)
    }

    fun move(newCoords: Coordinates): PlayerData {

        var numberOfTreasuresReached = numberOfTreasuresReached
        val reachesGoalInCurrentMove = newCoords == goalPosition && currentPosition != goalPosition
        val isLastGoalAndGoalSameAsHome = pursuingLastGoal && goalPosition == homePosition

        if (reachesGoalInCurrentMove && !isLastGoalAndGoalSameAsHome) {
            numberOfTreasuresReached++
        }

        return this.copy(
            currentPosition = newCoords,
            hasReachedGoal = hasReachedGoal || (newCoords == goalPosition && currentPosition != goalPosition),
            numberOfTreasuresReached = numberOfTreasuresReached
        )
    }


    fun assignNewGoal(newCoords: Coordinates, foundTreasure: Boolean, isLastGoal: Boolean): PlayerData {

        return this.copy(
            goalPosition = newCoords,
            hasReachedGoal = foundTreasure, // newly assigned treasure is not found yet
            pursuingLastGoal = isLastGoal
        )
    }
}


/**
 * Public information about the player data.
 */
data class PublicPlayerData(
    var name: String,
    var currentPosition: Coordinates,
    val homePosition: Coordinates,
    val color: Color,
    val numberOfTreasuresReached: Int
)
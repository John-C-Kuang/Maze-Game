package Common.tile

import Common.tile.treasure.Treasure
import java.util.*

/**
 * Represents a tile on a Maze Board. Every tile has a path, degree for which it's been rotated, and treasure for players
 * to collect. A tile has outgoing and incoming directions (the inverse of outgoing) to determine connectability with
 * other tiles.
 */
data class GameTile(val path: Path, val degree: Degree, val treasure: Treasure) {
    private val outgoingDirections: Set<Direction> = path.getDefaultOutgoingDirections().map {
            outGoingDirection -> outGoingDirection.rotateBy(degree)
    }.toSet()
    private val incomingDirections: Set<Direction> = outgoingDirections.map { outgoingDirection -> outgoingDirection.reverse() }.toSet()

    /**
     * Rotates the game tile by the given degree, returning a new GameTile.
     */
    fun rotate(degree: Degree): GameTile {
        return GameTile(this.path, this.degree.add(degree), this.treasure)
    }

    /**
     * The directions that one can go to from a tile.
     *
     * Example:
     * │ can go UP and DOWN
     * ┐ can go LEFT and DOWN
     * └ can go UP and RIGHT
     */
    fun getOutgoingDirections(): Set<Direction> {
        return outgoingDirections
    }

    /**
     * The directions that one can reach a tile from.
     *
     * Computed by taking the inverse of all outgoingDirections.
     *
     * Example:
     * │ can be reached from UP and DOWN
     * ┐ can be reached from RIGHT and UP
     * └ can be reached from DOWN and LEFT
     */
    fun getIncomingDirections(): Set<Direction> {
        return incomingDirections
    }


    /**
     * Looking from outside the tile, going in, can this tile be reached through this direction.
     *
     * Ex:
     * │ can be reached from UP and DOWN
     * ┐ can be reached from RIGHT and UP
     * └ can be reached from DOWN and LEFT
     */
    fun canBeReachedFrom(incomingDirection: Direction): Boolean {
        return this.incomingDirections.contains(incomingDirection)
    }

    override fun toString(): String {
        return stringRepresentation[Pair(this.path, this.degree)]
            ?: throw IllegalArgumentException("Could not print tile with path: ${this.path} and degree: ${this.degree}")
    }

    override fun hashCode(): Int {
        return Objects.hash(this.treasure, this.path)
    }

    override fun equals(other: Any?): Boolean {
        if (other is GameTile) {
            return this.treasure.equals(other.treasure) && this.path.equals(other.path)
        }
        return false
    }


    companion object {
        val stringRepresentation = mapOf(
            Pair(Path.VERTICAL, Degree.ZERO) to "│",
            Pair(Path.VERTICAL, Degree.ONE_EIGHTY) to "│",
            Pair(Path.VERTICAL, Degree.TWO_SEVENTY) to "─",
            Pair(Path.VERTICAL, Degree.NINETY) to "─",
            Pair(Path.UP_RIGHT, Degree.ZERO) to "└",
            Pair(Path.UP_RIGHT, Degree.NINETY) to "┘",
            Pair(Path.UP_RIGHT, Degree.ONE_EIGHTY) to "┐",
            Pair(Path.UP_RIGHT, Degree.TWO_SEVENTY) to "┌",
            Pair(Path.CROSS, Degree.ZERO) to "┼",
            Pair(Path.CROSS, Degree.NINETY) to "┼",
            Pair(Path.CROSS, Degree.ONE_EIGHTY) to "┼",
            Pair(Path.CROSS, Degree.TWO_SEVENTY) to "┼",
            Pair(Path.T, Degree.ZERO) to "┬",
            Pair(Path.T, Degree.NINETY) to "├",
            Pair(Path.T, Degree.ONE_EIGHTY) to "┴",
            Pair(Path.T, Degree.TWO_SEVENTY) to "┤")
    }
}


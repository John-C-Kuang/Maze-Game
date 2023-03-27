package Common.board

import Common.tile.HorizontalDirection
import Common.tile.VerticalDirection

/**
 * A Position is a valid index in the Maze board. The value of position must exist in the specified range. All boards
 * constructed needs its size to abide by the MIN and MAX INDEX constants.
 */
interface Position {
    val value: Int

    // throws an illegal argument exception if the given position noting the row/col is not slideable
    fun checkSlideable() {
        if (!isSlideable()) {
            throw IllegalArgumentException("Row/col ${this.value} is not slideable.")
        }
    }

    fun isSlideable(): Boolean {
        return (this.value % 2 == 0)
    }
}

/**
 * Represents a valid column index on a Maze board.
 */
data class ColumnPosition(override val value: Int): Position {
    /**
     * Gets the next column position based on a horizontal direction. Returns null if out of bounds.
     */
    fun nextPosition(direction: HorizontalDirection, max: Int): ColumnPosition? {
        val newVal = when (direction) {
            HorizontalDirection.LEFT-> value - 1
            HorizontalDirection.RIGHT -> value + 1
        }
        if (newVal < 0 || newVal > max) {
            return null
        }
        return ColumnPosition(newVal)
    }


    override fun toString(): String {
        return value.toString()
    }

}

/**
 * Represents a valid row index on a Maze board.
 */
data class RowPosition(override val value: Int): Position {

    /**
     * Gets the next row position based on a vertical direction. Returns null if it is out of bounds.
     */
    fun nextPosition(direction: VerticalDirection,max: Int): RowPosition? {
        val newVal = when (direction) {
            VerticalDirection.DOWN -> value + 1
            VerticalDirection.UP -> value - 1
        }
        if (newVal < 0 || newVal > max) {
            return null
        }
        return RowPosition(newVal)
    }

    override fun toString(): String {
        return value.toString()
    }

}
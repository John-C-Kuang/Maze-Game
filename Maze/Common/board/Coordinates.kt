package Common.board

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Represents a valid coordinate in the board. Rows and columns are indexed 0..6 and (0,0) is
 * top-left.
 */
data class Coordinates(
    val row: RowPosition,
    val col: ColumnPosition
) {

    /**
     * Returns a copy of this with a new row value.
     */
    fun copyWithNewRow(rowValue: Int): Coordinates {
        return this.copy(row = RowPosition(rowValue))
    }

    /**
     * Returns a copy of this with a new column value.
     */
    fun copyWithNewCol(colValue: Int): Coordinates {
        return this.copy(col = ColumnPosition(colValue))
    }

    override fun toString(): String {
        return "($row, $col)"
    }

    /**
     * Computes the Euclidian distance between this coordinates and another coordinate.
     */
    fun euclidDistanceTo(targetCoordinates: Coordinates): Double {
        val (row, col) = this
        val (targetRow, targetCol) = targetCoordinates
        val deltaX = (col.value - targetCol.value).toDouble()
        val deltaY = (row.value - targetRow.value).toDouble()
        return sqrt(deltaX.pow(2) + deltaY.pow(2))
    }

    companion object {
        /**
         * Utility function to construct a Coordinates object from a pair of Ints.
         * Throws IllegalArgumentException if the Ints are out of bounds.
         */
        fun fromRowAndValue(rowPos: Int, colPos: Int): Coordinates {
            return Coordinates(RowPosition(rowPos), ColumnPosition(colPos))
        }
    }
}





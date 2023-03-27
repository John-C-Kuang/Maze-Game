package Common.board

import Common.tile.Direction
import Common.tile.GameTile
import Common.tile.HorizontalDirection
import Common.tile.VerticalDirection
import Common.tile.treasure.Treasure
import java.util.*

/**
 * A Maze board that supports player operations on 2d representation of Maze game tiles.
 */
class Board(private val tiles: Array<Array<GameTile>>) {

    val height = tiles.size
    val width = tiles[0].size

    /**
     * Slides the row at the given row index in a certain horizontal direction. Insert a spare tile into the empty slot
     * generated from the slide. Returns the tile that is dislodged from board as a result of the slide.
     */
    fun slideRowAndInsert(rowPosition: RowPosition, direction: HorizontalDirection, spareTile: GameTile): Pair<Board, GameTile> {
        return slideAndInsertSpare(rowPosition, direction, spareTile)
    }

    /**
     * Slides the col at the given column index in a certain vertical direction. Inserts a spare tile into the empty slot
     * generated from the slide. Returns the tile that is dislodged from board as a result of the slide.
     */
    fun slideColAndInsert(columnPosition: ColumnPosition, direction: VerticalDirection, spareTile: GameTile): Pair<Board, GameTile> {
        return slideAndInsertSpare(columnPosition, direction, spareTile)
    }

    /**
     * Performs a search of all reachable tiles starting from _position_, neighbors are determined
     * by whether two adjacent tile's have connecting shapes.
     */
    fun getReachableTiles(startingPosition: Coordinates): Set<Coordinates>  {
        val stack = Stack<Coordinates>()
        val visitedNodes = HashSet<Coordinates>()
        stack.add(startingPosition)

        while (stack.isNotEmpty()) {
            val currentPosition = stack.pop()
            if (!visitedNodes.contains(currentPosition)) {
                getTile(currentPosition).getOutgoingDirections().forEach {
                    addReachableNeighborToPath(currentPosition, it, stack) }
                visitedNodes.add(currentPosition)
            }
        }
        return visitedNodes
    }

    /**
     * Gets the tile at the specified position.
     */
    fun getTile(position: Coordinates): GameTile {
        return tiles[position.row.value][position.col.value]
    }


    // checks if slideable, throws Exception if not, slides a row or column, rotates by the degree, then inserts the spare tile,
    // returning the dislodged tile
    private fun slideAndInsertSpare(position: Position, direction: Direction, spareTile: GameTile): Pair<Board, GameTile> {
        position.checkSlideable()
        val dislodgedTile = getDislodgedTile(position, direction)

        val newTiles = shiftByDirection(position,direction)

        val emptySlotPosition = getEmptySlotPositionAfterSliding(position, direction)

        setTile(newTiles, emptySlotPosition, spareTile)

        return Pair(Board(newTiles), dislodgedTile)
    }


    //get the tile to be dislodged if a slide was performed at the given position (row/col index)
    private fun getDislodgedTile(position: Position, direction: Direction): GameTile {
        return when (direction) {
            HorizontalDirection.LEFT -> tiles[position.value][0]
            HorizontalDirection.RIGHT -> tiles[position.value][this.width - 1]
            VerticalDirection.UP -> tiles[0][position.value]
            VerticalDirection.DOWN -> tiles[this.height - 1][position.value]
        }
    }

    // get the position of the empty slot after making a slide in a given direction
    fun getEmptySlotPositionAfterSliding(position: Position, direction: Direction): Coordinates {
        return when(direction) {
            HorizontalDirection.LEFT -> Coordinates(RowPosition(position.value), ColumnPosition(width - 1))
            HorizontalDirection.RIGHT -> Coordinates(RowPosition(position.value), ColumnPosition(0))
            VerticalDirection.UP -> Coordinates(RowPosition(height-1), ColumnPosition(position.value))
            VerticalDirection.DOWN -> Coordinates(RowPosition(0), ColumnPosition(position.value))
        }
    }

    fun getTilesInRow(rowPosition: RowPosition): List<GameTile> {
        return this.getAllColumns()
            .map { columnPosition ->  getTile(Coordinates(rowPosition, columnPosition)) }
    }

    // shifts a row/col in a certain direction
    private fun shiftByDirection(position: Position, direction: Direction): Array<Array<GameTile>> {
        val newTiles = copyOfTiles()
        for (index in getIterationRange(direction)) {
            setTileFromNextTile(newTiles, index, position, direction)
        }
        return newTiles
    }

    // gets the indices to be shifted in a given direction
    private fun getIterationRange(direction: Direction): Iterable<Int> {
        return when(direction) {
            HorizontalDirection.RIGHT -> width - 1  downTo 1
            HorizontalDirection.LEFT -> 0 until width-1
            VerticalDirection.DOWN -> height-1 downTo   1
            VerticalDirection.UP -> 0 until height-1
        }
    }

    // if sliding in certain directions, this function returns the index difference for shifting elements in an array
    private fun getDifference(direction: Direction): Int {
        return when(direction) {
            HorizontalDirection.LEFT, VerticalDirection.UP -> 1
            HorizontalDirection.RIGHT, VerticalDirection.DOWN -> -1
        }
    }

    // sets the current tile of the array to the next tile, given the direction
    private fun setTileFromNextTile(tiles: Array<Array<GameTile>>, index: Int,  position: Position, direction: Direction) {
        val difference = getDifference(direction)
        val currentTilePosition: Coordinates = getCurrentTilePositionInShift(direction, position, index)
        val nextTilePosition: Coordinates = getNextTilePositionInShift(direction, position, index, difference)
        setTile(tiles, currentTilePosition, getTile(nextTilePosition))
    }

    // gets the current tile position based on current row/col index and the current step index) of the shift
    private fun getCurrentTilePositionInShift(direction: Direction, position: Position, index: Int): Coordinates {
        return when (direction) {
            is HorizontalDirection -> Coordinates(RowPosition(position.value), ColumnPosition(index))
            is VerticalDirection -> Coordinates(RowPosition(index), ColumnPosition(position.value))
        }
    }

    // get the immediately adjacent tile during a shift, based on direction and position (row/col index).
    private fun getNextTilePositionInShift(direction: Direction, position: Position, index: Int, difference: Int): Coordinates {
        return when (direction) {
            is HorizontalDirection -> Coordinates(RowPosition(position.value ), ColumnPosition(index + difference))
            is VerticalDirection -> Coordinates(RowPosition(index + difference), ColumnPosition(position.value))
        }
    }

    // checks all of a tiles adjacent to a position, if they are reachable, adds them to
    // the stack
    private fun addReachableNeighborToPath(currentPosition: Coordinates, outgoingDirection: Direction,
                                           stack: Stack<Coordinates>) {
        getPositionAdjacentTo(currentPosition, outgoingDirection)?.let { neighborPosition ->
            val neighbor = getTile(neighborPosition)
            if (neighbor.canBeReachedFrom(outgoingDirection)) {
                stack.add(neighborPosition)
            }
        }
    }

    // gets the coordinates adjacent to a position in the provided direction. If the new position is out of bounds,
    // will return null. This is safe because the kotlin typechecker will force us to check if the
    // return type of this function is null.
    private fun getPositionAdjacentTo(position: Coordinates, outgoingDirection: Direction): Coordinates? {
        val rowValue = position.row.value
        val colValue = position.col.value

        val isAboveValid = outgoingDirection == VerticalDirection.UP && rowValue > 0
        val isBelowValid = outgoingDirection == VerticalDirection.DOWN && rowValue < this.height - 1
        val isLeftValid = outgoingDirection == HorizontalDirection.LEFT && colValue > 0
        val isRightValid = outgoingDirection == HorizontalDirection.RIGHT && colValue < this.width - 1

        return if (isAboveValid) {
            position.copyWithNewRow(rowValue - 1)
        } else if (isBelowValid) {
            position.copyWithNewRow(rowValue + 1)
        } else if (isLeftValid) {
            position.copyWithNewCol(colValue - 1)
        } else if (isRightValid) {
            position.copyWithNewCol(colValue + 1)
        } else {
            null
        }
    }

    /**
     * Sets the tile at the given position.
     */
    private fun setTile(tiles: Array<Array<GameTile>>, position: Coordinates, tile: GameTile) {
        val row = tiles[position.row.value]
        row[position.col.value] = tile
    }

    private fun copyOfTiles(): Array<Array<GameTile>> {
        return Array(this.height) { row -> Array(this.width) { col ->
                getTile(Coordinates.fromRowAndValue(row, col)).copy()
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Board) {
            return false
        }
        val otherTiles = other.tiles
        if (this.tiles.size != otherTiles.size || this.tiles[0].size != otherTiles[0].size) {
            return false
        }

        return this.tiles.mapIndexed{ index, row -> row.contentEquals(otherTiles[index])}.reduceRight{ a,b -> a && b}
    }

    override fun hashCode(): Int {
        return this.tiles.contentHashCode()
    }

    fun getAllMovableRows(): List<RowPosition> {
        return (tiles.indices step 2).map { RowPosition(it) }
    }

    fun getAllMovableColumns(): List<ColumnPosition> {
        return (0 until tiles[0].size step 2).map { ColumnPosition(it) }
    }

    fun getAllUnMovableRows(): List<RowPosition> {
        return (1 until height step 2).map { RowPosition(it) }
    }

    fun getAllUnMovableColumns(): List<ColumnPosition> {
        return (1 until tiles[0].size step 2).map { ColumnPosition(it) }
    }

    fun getAllRows(): List<RowPosition> {
        return tiles.indices.map { RowPosition(it) }
    }

    fun getAllColumns(): List<ColumnPosition> {
        return tiles[0].indices.map { ColumnPosition(it) }
    }

    fun getAllCoordinates(): List<Coordinates> {
        val allCoordinates = mutableListOf<Coordinates>()
        for (rowPosition in getAllRows()) {
            for (columnPosition in getAllColumns()) {
                allCoordinates.add(Coordinates(rowPosition, columnPosition))
            }
        }
        return allCoordinates
    }


    companion object {
        /**
         * Returns if the given 2d array of tiles are unique.
         */
        fun tilesAreValid(tiles: Array<Array<GameTile>>, width: Int, height: Int): Boolean {
            if (tiles.size != height) {
                return false
            }

            if (tiles[0].size != width) {
                return false
            }

            val gems = tiles.flatten().map { it.treasure }
            return Treasure.allTreasuresAreUnique(gems)
        }
    }
}

package Common.board

import Common.TestData.createBoard
import Common.TestData.createTiles
import Common.tile.*
import Common.tile.treasure.Gem
import Common.tile.treasure.Treasure
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import testing.TestUtils.getTilesInCol
import testing.TestUtils.getTilesInRow

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal class BoardTest {

    /**
     * Remove players from tile
     *
     * Find player location
     *
     * Get tile
     */

    @Test
    fun testSlideHorizontal() {
        //ARRANGE
        val tiles = createTiles()
        val board = createBoard(tiles)
        val spareTile = GameTile(Path.T, Degree.ZERO, Treasure(Gem.AMETRINE, Gem.AMETHYST))

        // ACT
        val boardAndTile = board.slideRowAndInsert(RowPosition(0), HorizontalDirection.RIGHT, spareTile)
        val newBoard = boardAndTile.first
        val spareTile2 = boardAndTile.second

        val firstRow = tiles[0]
        val expectedTiles = arrayOf(
            spareTile, firstRow[0], firstRow[1], firstRow[2], firstRow[3], firstRow[4], firstRow[5]
        )

        // ASSERT
        Assert.assertArrayEquals(expectedTiles, getTilesInRow(0, newBoard))

        // ACT
        val boardAndTile2 = board.slideRowAndInsert(RowPosition(2), HorizontalDirection.LEFT, spareTile2)
        val thirdRow = tiles[2]

        val expectedThirdRow = arrayOf(
            thirdRow[1], thirdRow[2], thirdRow[3], thirdRow[4], thirdRow[5], thirdRow[6], spareTile2
        )

        // ASSERT
        Assert.assertArrayEquals(expectedThirdRow, getTilesInRow(2, boardAndTile2.first))
    }


    @Test
    fun testSlideVerticalUP() {
        val tiles = createTiles()
        val board = createBoard(tiles)
        val spareTile = GameTile(Path.T, Degree.ZERO, Treasure(Gem.AMETRINE, Gem.AMETHYST))

        val newBoardAndTile = board.slideColAndInsert(ColumnPosition(0), VerticalDirection.UP, spareTile)
        val newBoard = newBoardAndTile.first

        val expectedTiles = arrayOf(
            tiles[1][0], tiles[2][0], tiles[3][0], tiles[4][0], tiles[5][0], tiles[6][0], spareTile
        )
        Assert.assertArrayEquals(expectedTiles, getTilesInCol(0, newBoard))
    }

    @Test
    fun testSlideVerticalDown() {
        val tiles = createTiles()
        val board = createBoard(tiles)
        val spareTile2 = GameTile(Path.CROSS, Degree.NINETY, Treasure(Gem.AMETRINE, Gem.AMETHYST))

        val newBoardAndTile = board.slideColAndInsert(ColumnPosition(6), VerticalDirection.DOWN, spareTile2)
        val expectedTiles = arrayOf(
            spareTile2, tiles[0][6], tiles[1][6], tiles[2][6], tiles[3][6], tiles[4][6], tiles[5][6]
        )
        Assert.assertArrayEquals(expectedTiles, getTilesInCol(6, newBoardAndTile.first))
    }



    @Test
    fun testSlideInvalidHorizontalPosition() {
        val board = createBoard()
        val someTile = GameTile(Path.UP_RIGHT, Degree.TWO_SEVENTY, Treasure(Gem.ALEXANDRITE, Gem.APATITE))
        assertThrows<IllegalArgumentException> { board.slideRowAndInsert(RowPosition(1), HorizontalDirection.RIGHT, someTile) }
        assertThrows<IllegalArgumentException> { board.slideRowAndInsert(RowPosition(3), HorizontalDirection.LEFT, someTile) }
    }

    @Test
    fun testSlideInvalidVerticalPosition() {
        val board = createBoard()
        val someTile = GameTile(Path.UP_RIGHT, Degree.TWO_SEVENTY, Treasure(Gem.ALEXANDRITE, Gem.APATITE))
        assertThrows<IllegalArgumentException> { board.slideColAndInsert(ColumnPosition(1), VerticalDirection.UP, someTile) }
        assertThrows<IllegalArgumentException> { board.slideColAndInsert(ColumnPosition(3), VerticalDirection.DOWN, someTile) }
    }


    @Test
    fun testGetReachableTilesUnreachable() {
        // ARRANGE
        val tiles = createTiles()
        val board = createBoard(tiles)

        // ACT
        val reachableFromTopLeft = board.getReachableTiles(Coordinates(RowPosition(1), ColumnPosition(1)))

        // ASSERT
        assertEquals(setOf(Coordinates.fromRowAndValue(1, 1)), reachableFromTopLeft)
    }

    @Test
    fun testGetUnreachableTilesSome() {
        // ARRANGE
        val tiles = createTiles()
        val board = createBoard(tiles)

        // ASSERT
        val reachableFromTopRight = board.getReachableTiles(Coordinates.fromRowAndValue(6, 6))
        assertEquals(setOf(Coordinates.fromRowAndValue(5, 4), Coordinates.fromRowAndValue(5,5),
            Coordinates.fromRowAndValue(6, 4), Coordinates.fromRowAndValue(6,5),
        Coordinates.fromRowAndValue(6,6)), reachableFromTopRight)
    }

    @Test
    fun testBoardEquals() {
        assert(createBoard() == createBoard())
    }

    @Test
    fun testCreate9x9Board() {
        val tiles = createTiles().sliceArray(0 until 4)
            .map { it.sliceArray(1 until 3)}.toTypedArray()

        val board = Board(tiles)

        assertEquals(2, board.width)
        assertEquals(4, board.height)

        assertEquals(
            listOf(RowPosition(0), RowPosition(2)),
            board.getAllMovableRows()
        )

        assertEquals(
            listOf(ColumnPosition(0)),
            board.getAllMovableColumns()
        )
    }
}


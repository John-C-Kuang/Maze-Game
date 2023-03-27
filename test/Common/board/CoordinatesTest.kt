package Common.board

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class CoordinatesTest {

    private val someRowPosition = RowPosition(2)
    private val someColPosition = ColumnPosition(3)
    private val someCoordinate = Coordinates(someRowPosition, someColPosition)

    @Test
    fun testCreateRowPosition() {
        val rowPosition = RowPosition(3)
        assertEquals(3, rowPosition.value)
    }

    @Test
    fun testCreateColPosition() {
        val colPosition = ColumnPosition(3)
        assertEquals(3, colPosition.value)
    }


    @Test
    fun testCopyWithNewRow() {
        assertEquals(Coordinates(RowPosition(0), someColPosition), someCoordinate.copyWithNewRow(0))
    }

    @Test
    fun testCopyWithNewCol() {
        assertEquals(Coordinates(someRowPosition, ColumnPosition(6)), someCoordinate.copyWithNewCol(6))
    }
}
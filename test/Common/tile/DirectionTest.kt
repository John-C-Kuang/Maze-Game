package Common.tile

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class DirectionTest {

    private val L = HorizontalDirection.LEFT
    private val R = HorizontalDirection.RIGHT
    private val U = VerticalDirection.UP
    private val D = VerticalDirection.DOWN

    @Test
    fun testReverse() {
        assertEquals(R, L.reverse())
        assertEquals(L, R.reverse())
        assertEquals(U, D.reverse())
        assertEquals(D, U.reverse())
    }

    @Test
    fun getDegreeFromDirection() {
        assertEquals(Degree.ZERO, R.getDegree())
        assertEquals(Degree.NINETY, U.getDegree())
        assertEquals(Degree.ONE_EIGHTY, L.getDegree())
        assertEquals(Degree.TWO_SEVENTY, D.getDegree())
    }

    @Test
    fun getDirectionFromDegree() {
        assertEquals(R, Direction.fromDegree(Degree.ZERO))
        assertEquals(L, Direction.fromDegree(Degree.ONE_EIGHTY))
        assertEquals(U, Direction.fromDegree(Degree.NINETY))
        assertEquals(D, Direction.fromDegree(Degree.TWO_SEVENTY))
    }

    @Test
    fun rotateByZero() {
        assertEquals(R, R.rotateBy(Degree.ZERO))
        assertEquals(L, L.rotateBy(Degree.ZERO))
        assertEquals(D, D.rotateBy(Degree.ZERO))
        assertEquals(U, U.rotateBy(Degree.ZERO))
    }

    @Test
    fun rotateBy() {
        assertEquals(U, R.rotateBy(Degree.NINETY))
        assertEquals(U, D.rotateBy(Degree.ONE_EIGHTY))
        assertEquals(D, R.rotateBy(Degree.TWO_SEVENTY))
        assertEquals(L, R.rotateBy(Degree.ONE_EIGHTY))
        assertEquals(R, L.rotateBy(Degree.ONE_EIGHTY))
    }

}
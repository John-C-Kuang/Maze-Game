package Common.tile

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

internal class DegreeTest {


    @Test
    fun testDegreeAdd() {
        val zero = Degree.ZERO
        val ninety = Degree.NINETY
        val oneeighty = Degree.ONE_EIGHTY
        val twoseventy = Degree.TWO_SEVENTY

        assertEquals(oneeighty, ninety.add(ninety))
        assertEquals(twoseventy, twoseventy.add(zero))
        assertEquals(zero, twoseventy.add(ninety))
        assertEquals(twoseventy, oneeighty.add(ninety))
    }

    @Test
    fun testDegreeValueOf() {
        assertEquals(Degree.ZERO, Degree.valueOf(0))
        assertEquals(Degree.NINETY, Degree.valueOf(90))
        assertEquals(Degree.ONE_EIGHTY, Degree.valueOf(180))
        assertEquals(Degree.TWO_SEVENTY, Degree.valueOf(270))
    }

    @Test
    fun testDegreeValueOfOver360() {
        assertEquals(Degree.ZERO, Degree.valueOf(360))
        assertEquals(Degree.ONE_EIGHTY, Degree.valueOf(360 + 180))
    }

    @Test
    fun testDegreeInvalid() {
        assertThrows<IllegalArgumentException> {
            Degree.valueOf(10)
        }
        assertThrows<IllegalArgumentException> {
            Degree.valueOf(70)
        }
        assertThrows<IllegalArgumentException> {
            Degree.valueOf(89)
        }
    }
}
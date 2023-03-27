package Common.tile.treasure

import Common.TestData.treasureStrings
import org.junit.jupiter.api.Test
import serialization.converters.TreasureConverter
import kotlin.test.assertTrue

internal class TreasureTest {

    @Test
    fun testTreasureUnordered() {
        val g1 = Gem.APATITE
        val g2 = Gem.HEMATITE

        val treasure1 = Treasure(g1, g2)
        val treasure1Rev = Treasure(g2, g1)

        assert(treasure1 == treasure1Rev)
    }

    @Test
    fun testTreasureEqual() {
        val g1 = Gem.APATITE
        val g2 = Gem.HEMATITE
        val g3 = Gem.APLITE

        assert(Treasure(g1, g2) != Treasure(g2, g3))
        assert(Treasure(g1, g2) == Treasure(g2, g1))
        assert(Treasure(g3, g1) == Treasure(g1, g3))
    }

    @Test
    fun testTreasureDuplicate() {
        val g1 = Gem.ZIRCON
        val g2 = Gem.ZOISITE


        val treasure1 = Treasure(g1, g1)
        val treasure2 = Treasure(g1, g1)

        assert(treasure1 == treasure2)
    }

    @Test
    fun testTreasureDuplicateInvalid() {
        val g1 = Gem.ZIRCON
        val g2 = Gem.ZOISITE
        val g3 = Gem.HACKMANITE


        val treasure1 = Treasure(g1, g1)
        val treasure2 = Treasure(g2, g3)
        val treasure3 = Treasure(g1, g3)
        val treasure4 = Treasure(g2, g1)

        assert(treasure1 != treasure2)
        assert(treasure1 != treasure3)
        assert(treasure1 != treasure4)
    }

    @Test
    fun testAreAllTreasuresUnique() {
        assertTrue(Treasure.allTreasuresAreUnique(
            TreasureConverter.getTreasuresFromStrings(treasureStrings).fold(listOf())
        { accum, list -> accum + list }))
    }

}
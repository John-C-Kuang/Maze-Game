package serialization.converters

import Common.tile.treasure.Gem
import Common.tile.treasure.Treasure

object TreasureConverter {

    fun getTreasuresFromStrings(treasures: List<List<List<String>>>): List<List<Treasure>> {
        return treasures.map { it.map { pair -> getTreasureFromString(pair[0], pair[1]) } }
    }

    fun getTreasureFromString(image1: String, image2: String): Treasure {
        val gem1 = Gem.valueOf(image1.toUpperCase().replace("-","_"))
        val gem2 = Gem.valueOf(image2.toUpperCase().replace("-","_"))
        return Treasure(gem1, gem2)
    }
}
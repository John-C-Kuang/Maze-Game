package Common.tile.treasure

/**
 * Represents an unordered pair of Gems.
 */
data class Treasure(val gem1: Gem, val gem2: Gem) {

    override fun equals(other: Any?): Boolean {
        if (other is Treasure) {
            return (other.gem1 == gem1 && other.gem2 == gem2) || (other.gem1 == gem2 && other.gem2 == gem1)
        }
        return false
    }

    override fun hashCode(): Int {
        return gem1.hashCode() + gem2.hashCode()
    }

    companion object {
        fun allTreasuresAreUnique(gems: List<Treasure>): Boolean {
            val setOfGems = mutableSetOf<Treasure>()
            for (gem in gems) {
                if (!setOfGems.contains(gem)) {
                    setOfGems.add(gem)
                } else {
                    return false
                }
            }
            return true
        }
    }
}
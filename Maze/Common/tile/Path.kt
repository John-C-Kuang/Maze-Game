package Common.tile


/**
 * Represents a canonical path on a tile.
 */
enum class Path(val symbol: String) {
    VERTICAL("│"), UP_RIGHT("└"), T("┬"), CROSS("┼");

    /**
     * Gives the set of directions an player can leave the tile on based on the path of the tile with no rotation.
     */
    fun getDefaultOutgoingDirections(): Set<Direction> {
        return when (this) {
            VERTICAL -> setOf(VerticalDirection.UP, VerticalDirection.DOWN)
            CROSS -> setOf(VerticalDirection.UP, VerticalDirection.DOWN, HorizontalDirection.LEFT, HorizontalDirection.RIGHT)
            UP_RIGHT -> setOf(VerticalDirection.UP, HorizontalDirection.RIGHT)
            T -> setOf(HorizontalDirection.LEFT, HorizontalDirection.RIGHT, VerticalDirection.DOWN)
        }
    }

}
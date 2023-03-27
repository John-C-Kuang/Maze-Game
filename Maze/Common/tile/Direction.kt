package Common.tile

/**
 * Represents the cardinal directions of UP, DOWN, LEFT, RIGHT for describing slides and directions on a path.
 */
sealed interface Direction {

    /**
     * Returns a new direction after applying the given degree to this direction.
     */
    fun rotateBy(degree: Degree): Direction {
        return fromDegree(this.getDegree().add(degree))
    }

    /**
     * Returns the opposite of this direction.
     */
    fun reverse(): Direction

    /**
     * Returns the default degree of a direction, used for rotation and the mapping between direction and degree in a path.
     */
    fun getDegree(): Degree

    companion object {
        /**
         * Returns the default direction from a degree, used for rotation and the mapping between direction and degree in a path.
         */
        fun fromDegree(degree: Degree): Direction {
            return when(degree) {
                Degree.ZERO -> HorizontalDirection.RIGHT
                Degree.NINETY -> VerticalDirection.UP
                Degree.ONE_EIGHTY -> HorizontalDirection.LEFT
                Degree.TWO_SEVENTY -> VerticalDirection.DOWN
            }
        }
    }

}
/**
 * Represents LEFT or RIGHT in slides and in path directions.
 */
enum class HorizontalDirection: Direction {
    LEFT, RIGHT;

    override fun getDegree(): Degree {
        return when(this) {
            RIGHT -> Degree.ZERO
            LEFT -> Degree.ONE_EIGHTY
        }
    }

    override fun reverse(): Direction {
        return when(this) {
            LEFT -> RIGHT
            RIGHT -> LEFT
        }
    }

    override fun toString(): String {
        return if (this == LEFT) "LEFT" else "RIGHT"
    }
}

/**
 * Represents UP or DOWN in slides and in path directions.
 */
enum class VerticalDirection: Direction {
    UP, DOWN;

    override fun getDegree(): Degree {
        return when(this) {
            UP -> Degree.NINETY
            DOWN -> Degree.TWO_SEVENTY
        }
    }

    override fun reverse(): Direction {
        return when(this) {
            UP-> DOWN
            DOWN -> UP
        }
    }

    override fun toString(): String {
        return if (this == UP) "UP" else "DOWN"
    }
}
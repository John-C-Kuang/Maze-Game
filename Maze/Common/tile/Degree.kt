package Common.tile

/**
 * Represents a rotation of a tile in increments of 90, 180, 270, and 0 degrees.
 */
enum class Degree(val value: Int) {
    ZERO(0), NINETY(90), ONE_EIGHTY(180), TWO_SEVENTY(270);

    /**
     * Returns a new Degree by summing this degree and the given Degree. Rotation is counter-clockwise.
     */
    fun add(other: Degree): Degree {
        return valueOf((this.value + other.value) % 360)
    }

    companion object {
        /**
         * Given an int, converts to degree. Throws an IllegalArgumentException if not a multiple of 90.
         */
        fun valueOf(int: Int): Degree {
            return when (int % 360) {
                0 -> ZERO
                90 -> NINETY
                180 -> ONE_EIGHTY
                270 -> TWO_SEVENTY
                else -> throw IllegalArgumentException("Only multiples of 90 are supported degree values.")
            }
        }
    }
}
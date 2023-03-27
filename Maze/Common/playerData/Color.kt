package Common.player

/**
 * Represents an avatar's unique color.
 */
sealed interface Color {
    companion object {
        private val currentColors = mutableSetOf<Color>()

        /**
         * Gets the Color from a string. Throws IllegalArgumentException
         */
        fun valueOf(color: String): Color {
            val color = if (BaseColor.isBaseColor(color)) BaseColor.valueOf(color.uppercase())
                else HexColor(color)

            currentColors.add(color)
            return color
        }
    }
}

/**
 * Represents a color from hexadecimal values.
 */
data class HexColor(val hexcode: String) : Color {
    override fun toString(): String {
        return hexcode
    }
}


//TODO: add hex values here for future use?
/**
 * Base colors denoted by a string.
 */
enum class BaseColor : Color {
    PURPLE,
    PINK,
    ORANGE,
    RED,
    BLUE,
    GREEN,
    YELLOW,
    BLACK,
    WHITE;

    override fun toString(): String {
        return when (this) {
            PURPLE -> "purple"
            PINK -> "pink"
            ORANGE -> "orange"
            RED -> "red"
            BLUE -> "blue"
            GREEN -> "green"
            YELLOW -> "yellow"
            BLACK -> "black"
            WHITE -> "white"
        }
    }

    companion object {
        fun isBaseColor(s: String): Boolean {
            return try {
                BaseColor.valueOf(s)
                true
            } catch (e: Exception) {
                false
            }
        }

    }
}
package serialization.data

import Common.board.Coordinates

data class CoordinateDTO(
    val `row#` : Int, val `column#`: Int
) {
    companion object {
        fun fromCoordinates(coordinates: Coordinates): CoordinateDTO {
            return CoordinateDTO(coordinates.row.value, coordinates.col.value)
        }
    }

    fun toCoordinate(): Coordinates {
        return Coordinates.fromRowAndValue(`row#`, `column#`)
    }
}
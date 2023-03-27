package serialization.converters

import Common.board.Coordinates
import serialization.data.CoordinateDTO

object CoordinateConverter {

    fun coordinateFromDTO(coordinateDTO: CoordinateDTO): Coordinates {
        return Coordinates.fromRowAndValue(coordinateDTO.`row#`, coordinateDTO.`column#`)
    }

    fun toDto(coordinates: Coordinates): CoordinateDTO {
        return CoordinateDTO(coordinates.row.value, coordinates.col.value)
    }
}
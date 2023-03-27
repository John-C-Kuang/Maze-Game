package serialization.converters

import Common.*
import Common.board.ColumnPosition
import Common.board.Coordinates
import Common.board.RowPosition
import Common.tile.Degree
import Common.tile.HorizontalDirection
import Common.tile.VerticalDirection
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import serialization.data.CoordinateDTO
import serialization.data.DirectionDTO

object ActionConverter {
    fun getLastMovingAction(data: List<String>?): MovingAction? {
        return if (data == null) {
            null
        } else {
            val index = data[0].toInt()
            val direction = DirectionDTO.valueOf(data[1])

            val zero = Degree.ZERO
            val zerozero = Coordinates.fromRowAndValue(0, 0)
            when (direction) {
                DirectionDTO.LEFT-> RowAction(RowPosition(index), HorizontalDirection.LEFT, zero, zerozero)
                DirectionDTO.RIGHT -> RowAction(RowPosition(index), HorizontalDirection.RIGHT, zero, zerozero)
                DirectionDTO.UP -> ColumnAction(ColumnPosition(index), VerticalDirection.UP, zero, zerozero)
                DirectionDTO.DOWN -> ColumnAction(ColumnPosition(index), VerticalDirection.DOWN, zero, zerozero)
            }
        }
    }

    fun serializeAction(action: MovingAction?): List<String>? {
        return when (action) {
            is ColumnAction ->
                listOf(action.columnPosition.value.toString(), action.direction.toString())
            is RowAction ->
                listOf(action.rowPosition.value.toString(), action.direction.toString())
            else -> null
        }
    }

    fun deserializeChoice(choice: JsonElement, gson: Gson): Action {
        return if (choice.isJsonPrimitive && choice.asJsonPrimitive.asString == "PASS") {
            Skip
        } else if (choice.isJsonArray && choice.asJsonArray.size() == 4) {
            val arr = choice.asJsonArray
            val index = arr.get(0).asInt
            val direction = when (DirectionDTO.valueOf(arr.get(1).asString)) {
                DirectionDTO.LEFT -> HorizontalDirection.LEFT
                DirectionDTO.RIGHT -> HorizontalDirection.RIGHT
                DirectionDTO.UP -> VerticalDirection.UP
                DirectionDTO.DOWN -> VerticalDirection.DOWN
            }
            val rotation = Degree.valueOf(arr.get(2).asInt)
            val coords = CoordinateConverter.coordinateFromDTO(
                gson.fromJson(arr.get(3), CoordinateDTO::class.java)
            )

            when (direction) {
                is HorizontalDirection -> RowAction(RowPosition(index), direction, rotation, coords)
                is VerticalDirection -> ColumnAction(ColumnPosition(index), direction, rotation, coords)
                else -> throw IllegalArgumentException("Not a direction")
            }

        } else {
            throw IllegalArgumentException("Invalid choice: $choice")
        }
    }

    fun serializeChoice(choice: Common.Action, gson: Gson): JsonElement {
        return when(choice) {
            Skip -> JsonPrimitive("PASS")
            is ColumnAction ->
                gson.toJsonTree(
                    listOf(
                        choice.columnPosition.value,
                        choice.direction,
                        choice.rotation.value,
                        CoordinateDTO.fromCoordinates(choice.newPosition)
                    )
                )
            is RowAction -> gson.toJsonTree(
                listOf(
                    choice.rowPosition.value,
                    choice.direction,
                    choice.rotation.value,
                    CoordinateDTO.fromCoordinates(choice.newPosition)
                )
            )
        }
    }
}
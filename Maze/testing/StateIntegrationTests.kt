package testing

import Common.GameState
import Common.board.ColumnPosition
import Common.board.RowPosition
import Common.tile.Degree
import Common.tile.HorizontalDirection
import Common.tile.VerticalDirection
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import serialization.converters.BoardConverter
import serialization.converters.PlayerConverter
import serialization.converters.TileConverter
import serialization.data.CoordinateDTO
import serialization.data.DirectionDTO
import serialization.data.StateDTO
import java.io.InputStreamReader

// state, index, direction, degree
fun main() {
    val jsonReader = JsonReader(InputStreamReader(System.`in`, "UTF-8"))
    val gson = Gson()

    val stateDTO = gson.fromJson<StateDTO>(jsonReader, StateDTO::class.java)
    val index = gson.fromJson<Int>(jsonReader, Int::class.java)
    val direction = gson.fromJson<DirectionDTO>(jsonReader, DirectionDTO::class.java)
    val degree = Degree.valueOf(gson.fromJson<Int>(jsonReader, Int::class.java))

    val board = BoardConverter.getBoardFromBoardDTO(stateDTO.board)
    val spareTile = TileConverter.getTileFromDTO(stateDTO.spare)
    val players = stateDTO.plmt.map { PlayerConverter.playerFromDto(it, "dummy") }
    val referee = GameState(board, spareTile, players)

    val position = players[0].currentPosition
    when(direction) {
        DirectionDTO.LEFT -> referee.slideRowAndInsertSpare(RowPosition(index), HorizontalDirection.LEFT, degree, position)
        DirectionDTO.RIGHT -> referee.slideRowAndInsertSpare(RowPosition(index), HorizontalDirection.RIGHT, degree, position)
        DirectionDTO.UP -> referee.slideColumnAndInsertSpare(ColumnPosition(index), VerticalDirection.UP, degree, position)
        DirectionDTO.DOWN -> referee.slideColumnAndInsertSpare(ColumnPosition(index), VerticalDirection.DOWN, degree, position)
    }

    val reachablePositions = referee.getBoard().getReachableTiles(players[0].currentPosition).map { CoordinateDTO.fromCoordinates(it) }

    val comp = compareBy<CoordinateDTO>({ it.`row#`}, {it.`column#`})

    println(reachablePositions.sortedWith(comp).map { gson.toJson(it, CoordinateDTO::class.java).toString() })
}




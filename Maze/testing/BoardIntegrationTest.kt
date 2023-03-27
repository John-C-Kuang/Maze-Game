package testing

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import serialization.converters.BoardConverter
import serialization.converters.CoordinateConverter
import serialization.data.BoardDTO
import serialization.data.CoordinateDTO
import java.io.InputStreamReader

fun main() {
    val jsonReader = JsonReader(InputStreamReader(System.`in`, "UTF-8"))
    val gson = Gson()

    val testBoard = gson.fromJson<BoardDTO>(jsonReader, BoardDTO::class.java)
    val coordinates = gson.fromJson<CoordinateDTO>(jsonReader, CoordinateDTO::class.java)

    val board = BoardConverter.getBoardFromBoardDTO(testBoard)
    val testCoordinate = CoordinateConverter.coordinateFromDTO(coordinates)

    val reachablePositions = board.getReachableTiles(testCoordinate).map { CoordinateDTO.fromCoordinates(it) }

    val comp = compareBy<CoordinateDTO>({ it.`row#`}, {it.`column#`})

    println(reachablePositions.sortedWith(comp).map { gson.toJson(it, CoordinateDTO::class.java) })
}
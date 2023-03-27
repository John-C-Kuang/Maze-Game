package testing

import Common.PublicGameState
import Common.board.Board
import Common.board.Coordinates
import Common.player.Color
import Common.player.PlayerData
import Players.Euclid
import Players.MazeStrategy
import Players.Riemann
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import serialization.converters.ActionConverter
import serialization.converters.PlayerConverter
import serialization.converters.TileConverter
import serialization.converters.TreasureConverter
import serialization.data.CoordinateDTO
import serialization.data.PlayerDTO
import serialization.data.StateDTO
import java.io.InputStreamReader
import java.util.*

fun main() {
    val jsonReader = JsonReader(InputStreamReader(System.`in`, "UTF-8"))
    val gson = Gson()

    val strategyDesignation = gson.fromJson<StrategyDesignation>(jsonReader, StrategyDesignation::class.java)
    val stateDTO = gson.fromJson<StateDTO>(jsonReader, StateDTO::class.java)
    val target = gson.fromJson<CoordinateDTO>(jsonReader, CoordinateDTO::class.java)

    val playerState = getPlayerState(stateDTO)
    val currentPlayer = getCurrentPlayer(stateDTO.plmt[0], target.toCoordinate())
    val strategy = strategyDesignation.getStrategy(currentPlayer)

    val choice = strategy.decideMove(playerState)

    val output = ActionConverter.serializeChoice(choice, gson)

    println(output)
}

fun getPlayerState(stateDTO: StateDTO): PublicGameState {
    val currentBoard = Board(TileConverter.getTilesFromConnectorsAndTreasures(stateDTO.board.connectors,
        TreasureConverter.getTreasuresFromStrings(stateDTO.board.treasures)))
    val spare = TileConverter.getTileFromStringAndTreasure(
        stateDTO.spare.tilekey,
        TreasureConverter.getTreasureFromString(stateDTO.spare.image1, stateDTO.spare.image2)
    )
    val lastAction = ActionConverter.getLastMovingAction(stateDTO.last)
    return PublicGameState(currentBoard, spare, lastAction, PlayerConverter.playerFromDto(stateDTO.plmt[0], "dummy").toPublicPlayerData())
}

fun getCurrentPlayer(playerData: PlayerDTO, target: Coordinates): PlayerData {
    val id = UUID.randomUUID().toString()
    val playerCoord = Coordinates.fromRowAndValue(playerData.current.`row#`, playerData.current.`column#`)
    val homeCoord = Coordinates.fromRowAndValue(playerData.home.`row#`, playerData.home.`column#`)
    return PlayerData(id, playerCoord, target, homeCoord, Color.valueOf(playerData.color), numberOfTreasuresReached = 0)
}



enum class StrategyDesignation {
    Riemann, Euclid;

    fun getStrategy(player: PlayerData): MazeStrategy {
        return when(this) {
            Riemann -> Riemann(player)
            Euclid -> Euclid(player)
        }
    }
}

package serialization.converters

import Common.board.Coordinates
import Common.player.Color
import Common.player.PlayerData
import Common.player.PublicPlayerData
import Common.tile.treasure.Gem
import Common.tile.treasure.Treasure
import serialization.data.PlayerDTO

object PlayerConverter {
    fun playerFromDto(playerDTO: PlayerDTO, name: String): PlayerData {
        val goal = Treasure(Gem.GROSSULAR_GARNET, Gem.HACKMANITE) // random treasure
        val currentPosition = CoordinateConverter.coordinateFromDTO(playerDTO.current)
        val homePosition = CoordinateConverter.coordinateFromDTO(playerDTO.home)
        return PlayerData(
            name,
            currentPosition,
            Coordinates.fromRowAndValue(0,0),
            homePosition,
            Color.valueOf(playerDTO.color),
            numberOfTreasuresReached = 0

        )
    }

    fun serializePlayer(player: PlayerData): PlayerDTO {
        return PlayerDTO(
            CoordinateConverter.toDto(player.currentPosition),
            CoordinateConverter.toDto(player.homePosition),
            player.color.toString()
        )
    }

    fun serializePublicPlayer(player: PublicPlayerData): PlayerDTO {
        return PlayerDTO(
            CoordinateConverter.toDto(player.currentPosition),
            CoordinateConverter.toDto(player.homePosition),
            player.color.toString()
        )
    }
}
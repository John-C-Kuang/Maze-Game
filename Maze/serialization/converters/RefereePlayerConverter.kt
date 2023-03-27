package serialization.converters

import Common.player.Color
import Common.player.PlayerData
import serialization.data.RefereePlayerDTO

object RefereePlayerConverter {

    fun refereePlayerFromDTO(refereePlayerDTO: RefereePlayerDTO, name: String): PlayerData {
        return PlayerData(
            id = name,
            currentPosition = refereePlayerDTO.current.toCoordinate(),
            homePosition = refereePlayerDTO.home.toCoordinate(),
            goalPosition = refereePlayerDTO.goto.toCoordinate(),
            hasReachedGoal = false,
            color = Color.valueOf(refereePlayerDTO.color),
            numberOfTreasuresReached = 0
        )
    }

    fun serializeRefereePlayer(playerData: PlayerData): RefereePlayerDTO {
        return RefereePlayerDTO(
            CoordinateConverter.toDto(playerData.currentPosition),
            CoordinateConverter.toDto(playerData.homePosition),
            CoordinateConverter.toDto(playerData.getGoal()),
            playerData.color.toString()
        )
    }
}
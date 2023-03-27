package serialization.converters

import Common.PublicGameState
import serialization.data.StateDTO

object PublicGameStateConverter {

    fun getPublicGameStateFromDTO(stateDTO: StateDTO, playerName: String): PublicGameState {
        return PublicGameState(
            BoardConverter.getBoardFromBoardDTO(stateDTO.board),
            TileConverter.getTileFromDTO(stateDTO.spare),
            ActionConverter.getLastMovingAction(stateDTO.last),
            PlayerConverter.playerFromDto(stateDTO.plmt[0], playerName).toPublicPlayerData()
        )
    }

    fun serializeGameState(publicGameState: PublicGameState): StateDTO {
        return StateDTO(
            BoardConverter.serializeBoard(publicGameState.board),
            TileConverter.serializeTile(publicGameState.spareTile),
            listOf(PlayerConverter.serializePublicPlayer(publicGameState.publicPlayerData)),
            ActionConverter.serializeAction(publicGameState.lastAction)
        )
    }
}
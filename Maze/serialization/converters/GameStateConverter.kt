package serialization.converters

import Common.GameState
import serialization.data.RefereeStateDTO

object GameStateConverter {

    fun getRefereeStateFromDTO(state: RefereeStateDTO, names: List<String>): GameState {
        val board = BoardConverter.getBoardFromBoardDTO(state.board)
        val spareTile = TileConverter.getTileFromDTO(state.spare)
        val players = state.plmt.mapIndexed { index, it ->
            RefereePlayerConverter.refereePlayerFromDTO(it, names[index])
        }
        val action = ActionConverter.getLastMovingAction(state.last)
        return GameState(board, spareTile, players, action)
    }

    fun serializeGameState(gameState: GameState): RefereeStateDTO {
        val publicGameState = gameState.toPublicState()
        val board = gameState.getBoard()
        val serializedBoard = BoardConverter.serializeBoard(board)

        val serializedTile = TileConverter.serializeTile(publicGameState.spareTile)

        val serializedPlayers = gameState.getPlayersData().map { (_, player) ->
            RefereePlayerConverter.serializeRefereePlayer(player)
        }

        val serializedAction = ActionConverter.serializeAction(publicGameState.lastAction)

        return RefereeStateDTO(
            serializedBoard, serializedTile, serializedPlayers, serializedAction
        )
    }
}
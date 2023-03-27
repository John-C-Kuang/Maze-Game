package serialization.converters

import Common.board.Board
import Common.board.Coordinates
import serialization.data.BoardDTO

object BoardConverter {

    fun getBoardFromBoardDTO(boardDTO: BoardDTO): Board {
        return Board(
            TileConverter.getTilesFromConnectorsAndTreasures(
                boardDTO.connectors,
                TreasureConverter.getTreasuresFromStrings(boardDTO.treasures)
            )
        )
    }

    fun serializeBoard(board: Board): BoardDTO {
        val allTiles = board.getAllRows().map { row ->
            board.getAllColumns().map { col ->
                board.getTile(Coordinates(row, col))
            }
        }
        return BoardDTO(
            allTiles.map { row -> row.map { it.toString() } },
            allTiles.map { row -> row.map { listOf(it.treasure.gem1.toString(), it.treasure.gem2.toString()) } }
        )
    }
}
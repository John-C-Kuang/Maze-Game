package Referee

import Common.GameState
import Common.board.Board
import Common.board.Coordinates
import Common.player.BaseColor
import Common.player.PlayerData
import Common.tile.Degree
import Common.tile.GameTile
import Common.tile.Path
import Common.tile.treasure.Gem
import Common.tile.treasure.Treasure
import Players.PlayerMechanism

class RandomStateReferee: Referee() {
    override fun createStateFromChosenBoard(suggestedBoards: List<Board>, players: List<PlayerMechanism>): GameState {
       return randomState(players)
    }

    fun randomState(players: List<PlayerMechanism>): GameState {
        val allTreasures = makeNRandomAndUniqueTreasures(WIDTH * HEIGHT + 1)
        val board = randomBoard(allTreasures)
        return GameState(
            board,
            randomTile(allTreasures[WIDTH * HEIGHT + 1]),
            makeRandomPlayers(board, players)
        )
    }

    private fun randomBoard(allTreasures: List<Treasure>): Board {
        val tiles = Array(HEIGHT) { rowIndex ->
            Array(WIDTH) { colIndex ->
                randomTile(allTreasures[rowIndex + colIndex * WIDTH])
            }
        }
        return Board(tiles)

    }

    private fun makeNRandomAndUniqueTreasures(treasureCount: Int): List<Treasure> {
        val treasures = mutableListOf<Treasure>()
        val gems = Gem.values()
        for (i in 0 .. treasureCount) {
            treasures.add(Treasure(gems[0], gems[i + 1]))
        }
        return treasures
    }

    private fun randomTile(treasure: Treasure): GameTile {
        val randomPath = Path.values().random()
        val randomDegree = Degree.values().random()
        return GameTile(randomPath, randomDegree, treasure)
    }

    private fun makeRandomPlayers(board: Board, playerMechanisms: List<PlayerMechanism>): List<PlayerData> {
        val unmovableRows = board.getAllUnMovableRows()
        val unmovableColumns = board.getAllUnMovableColumns()
        val colors = BaseColor.values()
        return playerMechanisms.mapIndexed{ index, player ->
            val homeRow = unmovableRows[index % unmovableRows.size]
            val homecol = unmovableColumns[index / unmovableColumns.size]
            val home = Coordinates(homeRow, homecol)
            val treasurePos = Coordinates(
                unmovableRows.random(),
                unmovableColumns.random()
            )
            PlayerData(
                player.name,
                currentPosition = home,
                goalPosition = treasurePos,
                homePosition = home,
                color = colors[index], // TODO: need hex color if run out
                numberOfTreasuresReached = 0
            )
        }
    }
}
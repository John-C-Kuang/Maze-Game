package Common

import Common.board.Board
import Common.board.Coordinates
import Common.player.BaseColor
import Common.player.PlayerData
import Common.tile.Degree
import Common.tile.GameTile
import Common.tile.Path
import Common.tile.treasure.Gem
import Common.tile.treasure.Treasure
import serialization.converters.TileConverter
import serialization.converters.TreasureConverter

object TestData {

    fun createGameStateWithPlayers(vararg players: PlayerData): GameState {
        val tiles = createTiles()

        val board = createBoard(tiles)

        return GameState(board, createSpareTile(), players.toList())
    }

    fun createGameStateWithPlayers(players: List<PlayerData>): GameState {
        val tiles = createTiles()

        val board = createBoard(tiles)

        return GameState(board, createSpareTile(), players)
    }

    fun createGameState(): GameState {
        return createGameState(createBoard())
    }

    fun createGameState(board: Board): GameState {
        val player1 = createPlayer1()

        val player2 = createPlayer2()

        val player3 = createPlayer3()
        return GameState(board, createSpareTile(), listOf(player1, player2, player3))
    }

    fun createGameState(tiles: Array<Array<GameTile>>): GameState {
        return createGameState(createBoard(tiles))
    }


    fun createTiles(): Array<Array<GameTile>> {
        return TileConverter.getTilesFromConnectorsAndTreasures(board,
            TreasureConverter.getTreasuresFromStrings(treasureStrings))
    }

    fun createSpareTile(): GameTile {
        return GameTile(Path.T, Degree.ONE_EIGHTY, Treasure(Gem.AMETHYST, Gem.AMETRINE))
    }

    fun createSpareTile(path: Path): GameTile {
        return GameTile(path, Degree.ZERO, Treasure(Gem.AMETHYST, Gem.AMETRINE))
    }


    fun createBoard(tiles: Array<Array<GameTile>>): Board {
        return Board(tiles)
    }

    fun createBoard(connectors: List<List<String>>): Board {
        val tiles = TileConverter.getTilesFromConnectorsAndTreasures(connectors,
            TreasureConverter.getTreasuresFromStrings(this.treasureStrings))
        return createBoard(tiles)
    }

    fun createBoard(): Board {
        return Board(createTiles())
    }

    val board = listOf(
            listOf("│", "─", "┐", "└", "┌", "┘", "└"),
            listOf("┘", "│", "┌",  "─", "┐", "┬", "┬"),
            listOf("┘", "┌", "│",  "┐", "└", "─", "┬"),
            listOf("┘", "│", "┌",  "─", "┐", "└", "┬"),
            listOf("┘", "│", "┌",  "─", "┐", "└", "┬"),
            listOf("│", "─", "┐", "└", "┌", "┘", "┬"),
            listOf("┘", "┌", "│",  "┐", "└", "─", "┬")
    )

    val easyToWinBoard = listOf(
        listOf("┼", "┼", "┼", "┼", "┼", "┼", "┼"),
        listOf("┼", "┼", "┼", "┼", "┼", "┼", "┼"),
        listOf("┼", "┼", "┼", "┼", "┼", "┼", "┼"),
        listOf("┘", "┘", "┘", "┘", "┘", "┘", "┘"),
        listOf("┘", "┘", "┘", "┘", "┘", "┘", "┘"),
        listOf("┘", "┘", "┘", "┘", "┘", "┘", "┘"),
        listOf("┘", "┘", "┘", "┘", "┘", "┘", "┘")
    )

    val impossibleBoard = listOf(
        listOf("┘", "─", "└", "│", "┘", "─", "└"),
        listOf("│", "─", "│", "│", "│", "─", "│"),
        listOf("┐", "─", "┌", "│", "┐", "─", "┌"),
        listOf("─", "─", "─", "─", "─", "─", "─"),
        listOf("┘", "─", "└", "│", "┘", "─", "└"),
        listOf("│", "─", "│", "│", "│", "─", "│"),
        listOf("┐", "─", "┌", "│", "┐", "─", "┌")
    )

    val treasureStrings = listOf(
        listOf(listOf("grossular-garnet", "black-obsidian"), listOf("tigers-eye", "yellow-beryl-oval"),listOf("chrysoberyl-cushion", "color-change-oval"), listOf("sunstone", "prasiolite"),
            listOf("citrine", "purple-oval"), listOf("emerald", "heliotrope"), listOf("zircon", "pink-spinel-cushion")),
            listOf(listOf("grossular-garnet", "goldstone"), listOf("ruby-diamond-profile", "zoisite"), listOf("star-cabochon", "ruby"),
                listOf("chrome-diopside", "beryl"), listOf("moonstone", "rock-quartz"), listOf("kunzite-oval", "green-beryl"),
                listOf("emerald", "green-princess-cut")), listOf(listOf("stilbite", "orange-radiant"), listOf("padparadscha-sapphire", "rose-quartz"),
                listOf("purple-spinel-trillion", "garnet"), listOf("blue-ceylon-sapphire", "fancy-spinel-marquise"),
                listOf("apricot-square-radiant", "emerald"), listOf("rhodonite", "almandine-garnet"), listOf("tigers-eye", "apatite")),
            listOf(listOf("pink-opal", "australian-marquise"), listOf("dumortierite", "tourmaline-laser-cut"), listOf("hematite", "ametrine"),
                listOf("hackmanite", "carnelian"), listOf("citrine-checkerboard", "heliotrope"), listOf("green-princess-cut", "zircon"),
                listOf("pink-spinel-cushion", "yellow-heart")), listOf(listOf("red-spinel-square-emerald-cut", "carnelian"), listOf("ammolite", "green-beryl"),
                listOf("aquamarine", "blue-pear-shape"), listOf("clinohumite", "chrysoberyl-cushion"), listOf("red-spinel-square-emerald-cut",
                    "padparadscha-oval"), listOf("carnelian", "grandidierite"), listOf("raw-citrine", "golden-diamond-cut")),
            listOf(listOf("cordierite", "green-aventurine"), listOf("tanzanite-trillion", "chrome-diopside"), listOf("garnet", "morganite-oval"),
                listOf("golden-diamond-cut", "grossular-garnet"), listOf("tourmaline-laser-cut", "blue-pear-shape"), listOf("magnesite", "chrysolite"),
                listOf("purple-cabochon", "moss-agate")), listOf(listOf("spinel", "sunstone"), listOf("pink-spinel-cushion", "carnelian"),
                listOf("apricot-square-radiant", "jaspilite"), listOf("magnesite", "moonstone"), listOf("ametrine", "ruby"), listOf("citrine", "diamond"),
                listOf("blue-ceylon-sapphire", "chrysoberyl-cushion")))

    fun createPlayer(position: Coordinates, treasurePos: Coordinates, homePos: Coordinates): PlayerData {
        return PlayerData(
            "player",
            position,
            treasurePos,
            homePos,
            BaseColor.BLACK,
            numberOfTreasuresReached = 0
        )
    }

    fun createPlayer1(): PlayerData {
        return PlayerData(
            "player1",
            Coordinates.fromRowAndValue(0,0),
            Coordinates.fromRowAndValue(1,1),
            Coordinates.fromRowAndValue(1, 3),
            BaseColor.PURPLE,
            numberOfTreasuresReached = 0
        )
    }

    fun createPlayer2(): PlayerData {
        return PlayerData(
            "player2",
            Coordinates.fromRowAndValue(0, 2),
            Coordinates.fromRowAndValue(3,3),
            Coordinates.fromRowAndValue(3, 5),
            BaseColor.GREEN,
            numberOfTreasuresReached = 0
        )
    }

    fun createPlayer3(): PlayerData {
        return PlayerData(
            "player3",
            Coordinates.fromRowAndValue(6,6),
            Coordinates.fromRowAndValue(5,5),
            Coordinates.fromRowAndValue(5, 5),
            BaseColor.BLACK,
            numberOfTreasuresReached = 0
        )
    }

    private fun changeTile(tiles: Array<Array<GameTile>>, pos: Coordinates, func: (GameTile) -> GameTile) {
        changeTile(tiles, pos.row.value, pos.col.value, func)
    }

    private fun changeTile(tiles: Array<Array<GameTile>>, row:Int, col: Int, func: (GameTile) -> GameTile) {
        tiles[row][col] = func(tiles[row][col])
    }
}
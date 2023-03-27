package Client.javafx


import Common.GameState
import Common.board.Board
import Common.board.ColumnPosition
import Common.board.Coordinates
import Common.board.RowPosition
import Common.player.BaseColor
import Common.player.HexColor
import Common.player.PlayerData
import Common.tile.GameTile
import Common.tile.treasure.Gem
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment

const val TILE_WIDTH = 100.0
const val TILE_HEIGHT = 100.0
const val IMAGE_WIDTH = TILE_WIDTH / 5
const val BORDER_WIDTH = 5.0
const val BASE_TILE_PADDING = 1.0

/**
 * Draws a public game state. Including all the tiles with their gems, players and homes (if applicable) and
 * a spare tile next to it.
 */
fun renderGameState(gameState: GameState): Pair<VBox, StackPane> {
    val board = gameState.getBoard()
    val playerDataMap = gameState.getPlayersData()

    val boardImage = drawBoard(
        board,
        mapPositionToPlayerColors(playerDataMap) { it.currentPosition },
        mapPositionToPlayerColors(playerDataMap) { it.homePosition }
    )

    val spareTileImage = drawBoardTile(gameState.toPublicState().spareTile, null, mapOf(), mapOf())

    return Pair(boardImage, spareTileImage)
}


/**
 * Draws a board as a vertical box of rows.
 */
private fun drawBoard(board: Board, playerPosToColor: Map<Coordinates, List<Color>>,
                      playerHomeToColor: Map<Coordinates, List<Color>>): VBox {
    return VBox().apply {
        board.getAllRows().forEach { rowPosition ->
            val row = drawRow(board.getTilesInRow(rowPosition), rowPosition,
                playerPosToColor, playerHomeToColor)
            children.add(row)
        }
    }
}

/**
 * Draws a single row given the tiles on the row.
 */
private fun drawRow(tiles: List<GameTile>, rowPosition: RowPosition,  playerPosToColor: Map<Coordinates, List<Color>>
                    ,playerHomeToColor: Map<Coordinates, List<Color>>): Node {
    return HBox().apply {
        tiles.forEachIndexed { index, tile ->
            children.add(drawBoardTile(tile, Coordinates(rowPosition, ColumnPosition(index)), playerPosToColor, playerHomeToColor)) }
    }
}

/**
 * Draws a single tile with the avatars and/or homes on it.
 */
private fun drawBoardTile(tile: GameTile, tileCoord: Coordinates?, playerColorAndPos: Map<Coordinates, List<Color>>,
                          playerHomeToColor: Map<Coordinates, List<Color>>): StackPane {
    val stack = StackPane()
    stack.padding = Insets(BASE_TILE_PADDING)
    stack.alignment = Pos.CENTER

    val base = Rectangle(TILE_WIDTH, TILE_HEIGHT)
    val gemOneStack = getImageFromGem(tile.treasure.gem1, Pos.TOP_LEFT)
    val gemTwoStack = getImageFromGem(tile.treasure.gem2, Pos.BOTTOM_RIGHT)

    // border if there is a home
    playerHomeToColor[tileCoord]?.let {
        val background = getBackgroundOfTileForBorder()
        base.fill = it[0]
        stack.children.addAll(base, background)
    } ?: run {
        base.fill = Color.WHITE
        stack.children.add(base)
    }

    stack.children.add(
        getPathAsTextComponent(tile)
    )

    playerColorAndPos[tileCoord]?.let {
        addPlayerIconsToTile(it, stack)
    }
    return StackPane(stack, gemOneStack, gemTwoStack)
}


/**
 * Gets the image of the given gem aligned in certain position.
 */
private fun getImageFromGem(gem: Gem, pos: Pos): StackPane {
    val gemImage = Image("gems/${getImagePath(gem)}",
        IMAGE_WIDTH, IMAGE_WIDTH, true, false)
    val gemStack = StackPane(ImageView(gemImage))
    gemStack.alignment = pos
    gemStack.maxHeight = TILE_HEIGHT
    gemStack.maxWidth = TILE_WIDTH
    return gemStack
}

fun getImagePath(gem: Gem): String {
    return gem.toString().lowercase().replace("_","-") + ".png"
}


/**
 * Gets a plain white background used in junction with another square to create a pseudo-border.
 */
private fun getBackgroundOfTileForBorder(): Rectangle {
    val background = Rectangle(TILE_WIDTH - BORDER_WIDTH, TILE_HEIGHT - BORDER_WIDTH)
    background.fill = Color.WHITE
    return background
}

/**
 * Returns a text component rendering of the given tile.
 */
private fun getPathAsTextComponent(tile: GameTile): Text {
    val text = Text()
    text.text = tile.toString()
    text.font = Font.font(75.0)
    text.fill = Color.BLACK
    text.textAlignment = TextAlignment.CENTER
    return text
}

/**
 * Given a stackpane, adds an icon for every color in the supplied list of colors
 */
private fun addPlayerIconsToTile(playerColors: List<Color>, stack: StackPane) {
    val initCircleSize = 25.0
    val maxDifference = 5.0
    var circleSize = initCircleSize
    val difference = (initCircleSize / playerColors.size).coerceAtMost(maxDifference)
    playerColors.forEach { color ->
        val icon = Circle(circleSize)
        icon.fill = color
        stack.children.add(icon)
        circleSize -= difference
    }
}

/**
 * Helper function that creates a mapping from coordinates to lists of colors, representing the avatars on the tile.
 */
private fun mapPositionToPlayerColors(playerData: Map<String, PlayerData>, getPosition: (PlayerData) -> Coordinates):
        Map<Coordinates, List<Color>> {
    val coordsToColors = mutableMapOf<Coordinates, MutableList<Color>>()

    playerData.values.forEach { player ->
        val coords = getPosition(player)
        val javaFxColor = javaFxColorFromColor(player.color)
        if (coordsToColors.containsKey(coords)) {
            coordsToColors[coords]?.add(javaFxColor)
        } else {
            coordsToColors[coords] = mutableListOf(javaFxColor)
        }
    }
    return coordsToColors
}

/**
 * Gets the JavaFX Color object from our Color class.
 */
private fun javaFxColorFromColor(color: Common.player.Color): Color {
    return when(color) {
        is HexColor -> Color.web(color.hexcode)
        BaseColor.RED -> Color.RED
        BaseColor.BLUE -> Color.BLUE
        BaseColor.PURPLE -> Color.PURPLE
        BaseColor.YELLOW -> Color.YELLOW
        BaseColor.PINK -> Color.PINK
        BaseColor.WHITE -> Color.WHITE
        BaseColor.GREEN -> Color.GREEN
        BaseColor.ORANGE -> Color.ORANGE
        BaseColor.BLACK -> Color.BLACK
    }
}
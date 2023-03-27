package Common.state

import Common.GameState
import Common.TestData
import Common.TestData.board
import Common.board.Board
import Common.board.ColumnPosition
import Common.board.Coordinates
import Common.board.RowPosition
import Common.player.BaseColor
import Common.player.PlayerData
import Common.tile.Degree
import Common.tile.GameTile
import Common.tile.HorizontalDirection
import Common.tile.VerticalDirection
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import testing.TestUtils
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class StateTests {

    @Test
    fun testSlideHorizontal() {
        val tiles = TestData.createTiles()
        val spareTile = TestData.createSpareTile()
        val board = Board(tiles)
        val player = TestData.createPlayer1()
        val state = GameState(board, spareTile, listOf(player))

        val newState = state.slideRowAndInsertSpare(
            RowPosition(0), HorizontalDirection.RIGHT, Degree.ZERO,
            Coordinates.fromRowAndValue(1, 1)
        )

        val expectedNewFirstRow = arrayOf(
            spareTile, tiles[0][0], tiles[0][1], tiles[0][2], tiles[0][3], tiles[0][4], tiles[0][5]
        )
        Assertions.assertArrayEquals(expectedNewFirstRow, TestUtils.getTilesInRow(0, newState.getBoard()))

        assertEquals(
            Coordinates.fromRowAndValue(1, 1),
            newState.getActivePlayer().currentPosition
        )
    }


    @Test
    fun testSlideVertical() {
        val tiles = TestData.createTiles()
        val spareTile = TestData.createSpareTile()
        val board = Board(tiles)
        val player = TestData.createPlayer1()
        val state = GameState(board, spareTile, listOf(player))

        val newState = state.slideColumnAndInsertSpare(
            ColumnPosition(0), VerticalDirection.DOWN, Degree.ONE_EIGHTY, Coordinates.fromRowAndValue(0, 0)
        )
        val expectedNewFirstColumn = arrayOf(
            spareTile, tiles[0][0], tiles[1][0], tiles[2][0], tiles[3][0], tiles[4][0], tiles[5][0],
        )
        Assertions.assertArrayEquals(expectedNewFirstColumn, TestUtils.getTilesInCol(0, newState.getBoard()))

        assertEquals(
            Coordinates.fromRowAndValue(0, 0),
            newState.getActivePlayer().currentPosition
        )
    }

    @Test
    fun testSlideHorizontalAndVertical() {
        val tiles = TestData.createTiles()
        val spareTile = TestData.createSpareTile()
        val board = Board(tiles)
        val state = GameState(board, spareTile, listOf(TestData.createPlayer2()))

        val newState = state.slideRowAndInsertSpare(
            RowPosition(2),
            HorizontalDirection.RIGHT, Degree.ZERO,
            Coordinates.fromRowAndValue(0, 1)
        )
        val tileAtEndOfRow = tiles[2][6]
        val expectedNewRow = arrayOf(
            spareTile, tiles[2][0], tiles[2][1], tiles[2][2], tiles[2][3], tiles[2][4], tiles[2][5]
        )
        Assertions.assertArrayEquals(expectedNewRow, TestUtils.getTilesInRow(2, newState.getBoard()))

        val expectedNewCol = arrayOf(
            tiles[1][6], tiles[2][5], tiles[3][6], tiles[4][6], tiles[5][6], tiles[6][6], tileAtEndOfRow
        )
        val secondSate = newState.slideColumnAndInsertSpare(
            ColumnPosition(6), VerticalDirection.UP, Degree.ZERO,
            Coordinates.fromRowAndValue(0, 2)
        )

        Assertions.assertArrayEquals(expectedNewCol, TestUtils.getTilesInCol(6, secondSate.getBoard()))
    }

    @Test
    fun tesSlideAndInsertPlayerDislodged() {
        val tiles = TestData.createTiles()
        val spareTileToBeInserted = TestData.createSpareTile()
        val board = Board(tiles)
        val player = TestData.createPlayer3()

        val gameState = GameState(board, spareTileToBeInserted, listOf(player))

        val newState = gameState.slideColumnAndInsertSpare(
            ColumnPosition(6),
            VerticalDirection.DOWN,
            Degree.ONE_EIGHTY,
            Coordinates.fromRowAndValue(1, 6)
        )

        assertEquals(Coordinates.fromRowAndValue(1, 6), newState.getActivePlayer().currentPosition)
    }

    @Test
    fun testMovePlayersInSlide() {
        val player1 = TestData.createPlayer1()
        val player2 = TestData.createPlayer2().copy(
            currentPosition = Coordinates.fromRowAndValue(0, 3)
        )
        val state = TestData.createGameStateWithPlayers(player1, player2)

        val newState = state.slideRowAndInsertSpare(
            RowPosition(0),
            HorizontalDirection.RIGHT, Degree.ZERO,
            Coordinates.fromRowAndValue(1, 1)
        )
        val playerData = newState.getPlayersData()
        assertEquals(Coordinates.fromRowAndValue(1, 1), playerData["player1"]?.currentPosition)
        assertEquals(Coordinates.fromRowAndValue(0, 4), playerData["player2"]?.currentPosition)

        val secondState = newState.slideColumnAndInsertSpare(
            ColumnPosition(4), VerticalDirection.DOWN, Degree.NINETY,
            Coordinates.fromRowAndValue(0, 1)
        )
        val secondPlayerData = secondState.getPlayersData()
        assertEquals(Coordinates.fromRowAndValue(0, 1), secondPlayerData["player1"]?.currentPosition)
        assertEquals(Coordinates.fromRowAndValue(1, 4), secondPlayerData["player2"]?.currentPosition)
    }

    @Test
    fun testKickoutActivePlayer() {
        val player1 = TestData.createPlayer1()
        val player2 = TestData.createPlayer2()
        val player3 = TestData.createPlayer3()

        val state = GameState(TestData.createBoard(), TestData.createSpareTile(), listOf(player1, player2, player3))

        val secondState = state.kickOutActivePlayer()
        assertEquals(player2, secondState.getActivePlayer())

        val thirdState = secondState.kickOutActivePlayer()
        assertEquals(player3, thirdState.getActivePlayer())

        val lastState = thirdState.kickOutActivePlayer()
        assert(lastState.isGameOver())
    }

    @Test
    fun testGameOverAfterConsecutivePasses() {
        val player1 = TestData.createPlayer1()
        val player2 = TestData.createPlayer2()
        val player3 = TestData.createPlayer3()

        val state = GameState(TestData.createBoard(), TestData.createSpareTile(), listOf(player1, player2, player3))

        val finalState = state.endActivePlayerTurn(true).endActivePlayerTurn(true).endActivePlayerTurn(true)
        assert(finalState.isGameOver())
    }

    @Test
    fun testGameOverAfterConsecutivePassesWithKickingPlayer() {
        val player1 = TestData.createPlayer1()
        val player2 = TestData.createPlayer2()
        val player3 = TestData.createPlayer3()

        val state = GameState(TestData.createBoard(), TestData.createSpareTile(), listOf(player1, player2, player3))

        val removedPlayer1 = state.kickOutActivePlayer()
        val finalState = removedPlayer1.endActivePlayerTurn(true).endActivePlayerTurn(true)
        assertFalse(finalState.isGameOver())
    }

    @Test
    fun testInsertRotateZeroDegrees() {
        val player1 = TestData.createPlayer1()

        val gameState = GameState(TestData.createBoard(), TestData.createSpareTile(), listOf(player1))
        gameState.slideRowAndInsertSpare(
            RowPosition(6),
            HorizontalDirection.RIGHT, Degree.ZERO,
            Coordinates.fromRowAndValue(1, 0)
        )
        val tile = gameState.getBoard().getTile(Coordinates(RowPosition(6), ColumnPosition(0)))
        assertEquals(tile, GameTile(tile.path, Degree.ZERO, tile.treasure))
    }


    @Test
    fun testInsertRotateNinetyDegrees() {
        val player1 = TestData.createPlayer1()

        val gameState = GameState(TestData.createBoard(), TestData.createSpareTile(), listOf(player1))
        gameState.slideRowAndInsertSpare(
            RowPosition(6), HorizontalDirection.RIGHT, Degree.NINETY,
            Coordinates.fromRowAndValue(1, 0)
        )
        val tile = gameState.getBoard().getTile(Coordinates(RowPosition(6), ColumnPosition(0)))
        assertEquals(tile, GameTile(tile.path, Degree.NINETY, tile.treasure))
    }


    @Test
    fun testInsertRotateOneEightyDegrees() {
        val player1 = TestData.createPlayer1()

        val gameState = GameState(TestData.createBoard(), TestData.createSpareTile(), listOf(player1))
        gameState.slideRowAndInsertSpare(
            RowPosition(6),
            HorizontalDirection.RIGHT, Degree.ONE_EIGHTY,
            Coordinates.fromRowAndValue(1, 0)
        )
        val tile = gameState.getBoard().getTile(Coordinates(RowPosition(6), ColumnPosition(0)))
        assertEquals(tile, GameTile(tile.path, Degree.ONE_EIGHTY, tile.treasure))
    }


    @Test
    fun testInsertRotateTwoSeventyDegrees() {
        val player1 = TestData.createPlayer1()

        val gameState = GameState(TestData.createBoard(), TestData.createSpareTile(), listOf(player1))
        gameState.slideRowAndInsertSpare(
            RowPosition(6),
            HorizontalDirection.RIGHT,
            Degree.TWO_SEVENTY,
            Coordinates.fromRowAndValue(1, 0)
        )
        val tile = gameState.getBoard().getTile(Coordinates(RowPosition(6), ColumnPosition(0)))
        assertEquals(tile, GameTile(tile.path, Degree.TWO_SEVENTY, tile.treasure))
    }


    @Test
    fun testInsertRotateThreeSixtyDegrees() {
        val player1 = TestData.createPlayer1()
        val player2 = TestData.createPlayer2()
        val player3 = TestData.createPlayer3()

        val gameState = GameState(TestData.createBoard(), TestData.createSpareTile(), listOf(player1, player2, player3))
        gameState.slideRowAndInsertSpare(
            RowPosition(6),
            HorizontalDirection.RIGHT,
            Degree.ZERO.add(Degree.TWO_SEVENTY).add(Degree.NINETY),
            Coordinates.fromRowAndValue(1, 0)
        )
        val tile = gameState.getBoard().getTile(Coordinates(RowPosition(6), ColumnPosition(0)))
        assertEquals(tile, GameTile(tile.path, Degree.ZERO, tile.treasure))
    }

    @Test
    fun testIsValidColumnMove() {
        val board = TestData.createBoard()
        val gameState = TestData.createGameState(board)


        assert(
            gameState.isValidColumnMove(
                ColumnPosition(0),
                VerticalDirection.DOWN,
                Degree.ONE_EIGHTY,
                Coordinates.fromRowAndValue(0, 0)
            )
        )

        assertFalse(
            gameState.isValidColumnMove(
                ColumnPosition(0),
                VerticalDirection.DOWN,
                Degree.ONE_EIGHTY,
                Coordinates.fromRowAndValue(1, 0)
            )
        )
    }

    @Test
    fun testIsValidColumnMovedPoppedOff() {
        val board = TestData.createBoard()
        val gameState = TestData.createGameState(board)

        assertFalse(
            gameState.isValidColumnMove(
                ColumnPosition(0),
                VerticalDirection.UP,
                Degree.ZERO,
                Coordinates.fromRowAndValue(6, 0)
            )
        )
    }

    @Test
    fun allHomesMustBeUnique() {
        val player1 = TestData.createPlayer1()
        val player2 = TestData.createPlayer2()
        val player3 = TestData.createPlayer3()
        assertThrows<IllegalArgumentException>("All player homes must be unique.") {
            GameState(
                TestData.createBoard(),
                TestData.createSpareTile(),
                listOf(
                    player1,
                    player2.copy(homePosition = player1.homePosition),
                    player3
                )
            )
        }
    }

    @Test
    fun testUpdateCurrentPlayerGoal() {
        val zekai = PlayerData(
            "zekai",
            Coordinates.fromRowAndValue(0, 0),
            Coordinates.fromRowAndValue(1, 1),
            Coordinates.fromRowAndValue(1, 3),
            BaseColor.PURPLE,
            numberOfTreasuresReached = 0
        )
        var gameState = GameState(TestData.createBoard(), TestData.createSpareTile(), listOf(zekai))
        assertEquals(Coordinates.fromRowAndValue(1, 1), gameState.getActivePlayer().goalPosition)

        gameState = gameState.updateCurrentPlayerGoal(
            newGoal = Coordinates.fromRowAndValue(2, 2),
            foundTreasure = false,
            isLastGoal = false
        )
        assertEquals(Coordinates.fromRowAndValue(2, 2), gameState.getActivePlayer().goalPosition)
        assertFalse(gameState.getActivePlayer().hasReachedGoal)

        gameState = gameState.updateCurrentPlayerGoal(
            newGoal = Coordinates.fromRowAndValue(3, 3),
            foundTreasure = true,
            isLastGoal = false
        )
        assertEquals(Coordinates.fromRowAndValue(3, 3), gameState.getActivePlayer().goalPosition)
        assertTrue(gameState.getActivePlayer().hasReachedGoal)

    }
}
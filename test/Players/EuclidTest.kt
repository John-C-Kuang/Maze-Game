package Players

import Common.RowAction
import Common.TestData
import Common.board.Coordinates
import Common.board.RowPosition
import Common.player.PlayerData
import Common.tile.Degree
import Common.tile.HorizontalDirection
import Common.tile.Path
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


internal class EuclidTest: AbstractOrderingStrategyTests() {

    override fun createStrategy(player: PlayerData): MazeStrategy {
        return Euclid(player)
    }

    @Test
    fun testGetToClosestToTreasureWhenTreasureNotReachable() {
        val move = getMoveWithPlayerAtPosition(
            playerPosition = Coordinates.fromRowAndValue(3, 0),
            treasurePosition = Coordinates.fromRowAndValue(5, 1),
            homePosition = Coordinates.fromRowAndValue(5, 5),
            makeBoard(),
            TestData.createSpareTile(Path.VERTICAL),
            null
        )

        val expected = RowAction(
            RowPosition(4),
            HorizontalDirection.LEFT,
            Degree.ZERO,
            Coordinates.fromRowAndValue(4, 1)
        )

        assertEquals(expected, move)
    }

    @Test
    fun testGetToClosesThroughInsertedSpare() {
        val move = getMoveWithPlayerAtPosition(
            playerPosition = Coordinates.fromRowAndValue(0, 6),
            treasurePosition = Coordinates.fromRowAndValue(2, 6),
            homePosition = Coordinates.fromRowAndValue(5, 5),
            makeBoard(),
            TestData.createSpareTile(Path.UP_RIGHT),
            null
        )

        val expected = RowAction(
            RowPosition(0),
            HorizontalDirection.LEFT,
            Degree.ONE_EIGHTY,
            Coordinates.fromRowAndValue(1, 6)
        )

        assertEquals(expected, move)
    }

}
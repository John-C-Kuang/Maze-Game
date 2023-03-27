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

internal class RiemannTest: AbstractOrderingStrategyTests() {


    @Test
    fun testGetToTopLeftWhenTreasureIsNotReachable() {
       val move = getMoveWithPlayerAtPosition(
           playerPosition = Coordinates.fromRowAndValue(1, 1),
           treasurePosition = Coordinates.fromRowAndValue(5, 5),
           homePosition = Coordinates.fromRowAndValue(5, 5),
           makeBoard(),
           TestData.createSpareTile(Path.VERTICAL),
           null
       )

       val expected = RowAction(
           RowPosition(0),
           HorizontalDirection.RIGHT,
           Degree.ZERO,
           Coordinates.fromRowAndValue(0, 0)
       )

       assertEquals(expected, move)
   }

    @Test
    fun testGetAlternateTileThroughInsertedTile() {
        val move = getMoveWithPlayerAtPosition(
            playerPosition = Coordinates.fromRowAndValue(1, 1),
            treasurePosition = Coordinates.fromRowAndValue(5, 5),
            homePosition = Coordinates.fromRowAndValue(5, 5),
            makeBoard(),
            TestData.createSpareTile(),
            null
        )

        val expected = RowAction(
            RowPosition(0),
            HorizontalDirection.RIGHT,
            Degree.NINETY,
            Coordinates.fromRowAndValue(0, 0)
        )

        assertEquals(expected, move)
    }

    override fun createStrategy(player: PlayerData): MazeStrategy {
        return Riemann(player)
    }
}
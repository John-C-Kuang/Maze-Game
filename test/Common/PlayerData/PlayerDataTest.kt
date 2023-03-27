package Common.PlayerData

import Common.board.Coordinates
import Common.player.BaseColor
import Common.player.PlayerData
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class PlayerDataTest {
    @Test
    fun testGetGoal() {
        val zekai = PlayerData(
            "zekai",
            Coordinates.fromRowAndValue(0,0),
            Coordinates.fromRowAndValue(1,1),
            Coordinates.fromRowAndValue(1, 3),
            BaseColor.PURPLE,
            numberOfTreasuresReached = 0
        )

        assertEquals(Coordinates.fromRowAndValue(1, 1), zekai.getGoal())
    }

    @Test
    fun testEquals() {
        val zekai = PlayerData(
            "zekai",
            Coordinates.fromRowAndValue(0,0),
            Coordinates.fromRowAndValue(1,1),
            Coordinates.fromRowAndValue(1, 3),
            BaseColor.PURPLE,
            numberOfTreasuresReached = 0
        )
        val zekai2 = PlayerData(
            "zekai",
            Coordinates.fromRowAndValue(0,0),
            Coordinates.fromRowAndValue(1,1),
            Coordinates.fromRowAndValue(1, 3),
            BaseColor.PURPLE,
            numberOfTreasuresReached = 0
        )

        assertEquals(zekai, zekai2)
    }

    @Test
    fun testMoveToTargetPosition() {
        var zekai = PlayerData(
            "zekai",
            Coordinates.fromRowAndValue(0,0),
            Coordinates.fromRowAndValue(1,1),
            Coordinates.fromRowAndValue(1, 3),
            BaseColor.PURPLE,
            numberOfTreasuresReached = 0
        )
        assertEquals(Coordinates.fromRowAndValue(0,0), zekai.currentPosition)
        assertFalse(zekai.hasReachedGoal)
        assertEquals(0, zekai.numberOfTreasuresReached)

        zekai = zekai.move(Coordinates.fromRowAndValue(1, 1))

        assertEquals(Coordinates.fromRowAndValue(1,1), zekai.currentPosition)
        assertTrue(zekai.hasReachedGoal)
        assertEquals(1, zekai.numberOfTreasuresReached)
    }

    @Test
    fun testMoveToNormalPosition() {
        var zekai = PlayerData(
            "zekai",
            Coordinates.fromRowAndValue(0,0),
            Coordinates.fromRowAndValue(1,1),
            Coordinates.fromRowAndValue(1, 3),
            BaseColor.PURPLE,
            numberOfTreasuresReached = 0
        )
        assertEquals(Coordinates.fromRowAndValue(0,0), zekai.currentPosition)
        assertFalse(zekai.hasReachedGoal)
        assertEquals(0, zekai.numberOfTreasuresReached)

        zekai = zekai.move(Coordinates.fromRowAndValue(4, 4))

        assertEquals(Coordinates.fromRowAndValue(4, 4), zekai.currentPosition)
        assertFalse(zekai.hasReachedGoal)
        assertEquals(0, zekai.numberOfTreasuresReached)
    }

    @Test
    fun testAssignNewGoal() {
        var zekai = PlayerData(
            "zekai",
            Coordinates.fromRowAndValue(0,0),
            Coordinates.fromRowAndValue(1,1),
            Coordinates.fromRowAndValue(1, 3),
            BaseColor.PURPLE,
            numberOfTreasuresReached = 0
        )
        assertEquals(Coordinates.fromRowAndValue(1, 1), zekai.getGoal())
        zekai = zekai.assignNewGoal(
            newCoords = Coordinates.fromRowAndValue(2, 2),
            foundTreasure = true,
            isLastGoal = false
        )

        assertEquals(Coordinates.fromRowAndValue(2, 2), zekai.goalPosition)
        assertTrue(zekai.hasReachedGoal)
    }

    @Test
    fun `only update numberOfTreasuresReached when on goal`() {

        var chengyi = PlayerData(
            id = "Chengyi",
            currentPosition = Coordinates.fromRowAndValue(0, 0),
            goalPosition = Coordinates.fromRowAndValue(5, 5),
            homePosition = Coordinates.fromRowAndValue(3, 3),
            BaseColor.GREEN,
            numberOfTreasuresReached = 3
        )

        // Move Chengyi to the goal
        chengyi = chengyi.move(Coordinates.fromRowAndValue(5, 5))
        assertEquals(4, chengyi.numberOfTreasuresReached)

        // Chengyi stays at the goal. No increment in numberOfTreasuresReached
        chengyi = chengyi.move(Coordinates.fromRowAndValue(5, 5))
        assertEquals(4, chengyi.numberOfTreasuresReached)

        // Assign new goal to Chengyi
        chengyi = chengyi.assignNewGoal(
            newCoords = Coordinates.fromRowAndValue(3, 2),
            foundTreasure = false, isLastGoal = false
        )

        // numberOfTreasuresReached increments when Chengyi reaches the new goal
        chengyi = chengyi.move(Coordinates.fromRowAndValue(3, 2))
        assertEquals(5, chengyi.numberOfTreasuresReached)

        // Move Chengyi to a new spot. Change the goal to that spot.
        // Should not increment numberOfTreasuresReached
        chengyi = chengyi.move(Coordinates.fromRowAndValue(2, 1))
        assertEquals(5, chengyi.numberOfTreasuresReached)
        chengyi = chengyi.assignNewGoal(
            newCoords = Coordinates.fromRowAndValue(2, 1),
            foundTreasure = false, isLastGoal = true
        )
        assertEquals(5, chengyi.numberOfTreasuresReached)

    }
}
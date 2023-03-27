package Players

import Common.board.Coordinates
import Common.player.PlayerData

/**
 * The ordering strategy that weighs alternative tiles based on their distance to the player's goal tile.
 */
class Euclid(player: PlayerData): AbstractOrderingStrategy(
    compareBy({ coord -> euclidDistanceFromGoal(coord, player) }, { it.row.value }, { it.col.value }),
    player)


/**
 * Computes the distance to the player's goal tile.
 */
fun euclidDistanceFromGoal(targetCoords: Coordinates, player: PlayerData): Double {
    val goalPosition = player.getGoal()
    return targetCoords.euclidDistanceTo(goalPosition)
}
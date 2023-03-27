package Players

import Common.Action
import Common.PublicGameState

/**
 * To create any type of strategy for the game of Maze. Exposes a single function that
 * determines an Action from the current PublicGameState.
 */
interface MazeStrategy {
    fun decideMove(playerState: PublicGameState): Action

}
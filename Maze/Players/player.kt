package Players

import Common.Action
import Common.PublicGameState
import Common.board.Coordinates
import Common.tile.GameTile


/**
 * Abstraction layer class denoting a player's API, to be used to represent a player connecting to the game and the
 * API expected from it. RandomSeed is used for testing.
 */
interface PlayerMechanism {
    /**
     * Represents the name for instances of PlayerMechanism.
     * a getter getName is auto generated from the Kotlin compiler.
     */
    val name: String

    /**
     * Produces a potential Maze board to played on of the given dimensions.
     */
    fun proposeBoard0(rows: Int, columns: Int): Array<Array<GameTile>>

    /**
     * 2 functions:
     * 1.) Sets up the player with initial public game state and the target goal.
     * 2.) Tells the player to return home since it has found the treasure. (state will be null here)
     */
    fun setupAndUpdateGoal(state: PublicGameState?, goal: Coordinates)

    /**
     * Requests the user to offer a turn (PASS or SLIDE/ROTATE/MOVE) given a game state.
     */
    fun takeTurn(state: PublicGameState): Action

    /**
     * Notify the player if it has won or not.
     */
    fun won(hasPlayerWon: Boolean)
}


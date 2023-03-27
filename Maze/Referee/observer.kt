package Referee

import Common.GameState


/**
 * Represents a Maze observer. A referee will notify it of the latest state so that it can display on demand.
 */
interface ObserverMechanism {

    /**
     * Adds a new state to the queue of received state.
     */
    fun updateState(newState: GameState)

    /**
     * To notify an observer the game has finished.
     */
    fun gameOver()
}
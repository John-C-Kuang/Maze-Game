package testing

import Common.GameState
import Common.board.Board
import Players.PlayerMechanism
import Referee.Referee

class TestableReferee(private val initialState: GameState): Referee() {

    override fun createStateFromChosenBoard(suggestedBoards: List<Board>, players: List<PlayerMechanism>): GameState {
        return initialState
    }
}
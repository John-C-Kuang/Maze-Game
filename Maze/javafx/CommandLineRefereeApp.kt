package Client.javafx

import Players.StrategyPlayerMechanism
import Referee.EndgameData
import Referee.ObserverMechanism
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import serialization.converters.GameStateConverter
import serialization.converters.PlayerSpecConverter
import serialization.data.RefereeStateDTO
import testing.TestableReferee
import java.io.InputStreamReader
import java.util.LinkedList

/**
 * A RefereeObserverApplication that reads a game's intial state and player strategies from the command
 * line and executes a game.
 */
class CommandLineRefereeApp: RefereeObserverApplication() {

    override fun startGame(observer: ObserverMechanism): EndgameData {
        val jsonReader = JsonReader(InputStreamReader(System.`in`))
        val gson = Gson()

        val playerSpec = gson.fromJson<List<List<Any>>>(jsonReader, Any::class.java)
        val refereeState = gson.fromJson<RefereeStateDTO>(jsonReader, RefereeStateDTO::class.java)
        val playerNames = playerSpec.map { it[0].toString() }

        val state = GameStateConverter.getRefereeStateFromDTO(refereeState, playerNames)
        val playerMechanism: List<StrategyPlayerMechanism> = playerSpec.map { PlayerSpecConverter.getPlayerMechanism(it) }

        val referee = TestableReferee(state)

        referee.registerObserver(observer)
        observer.updateState(state)

        return referee.startGame(playerMechanism, LinkedList())
    }

}
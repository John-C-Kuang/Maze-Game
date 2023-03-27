package testing

import Common.GameState
import Players.StrategyPlayerMechanism
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import serialization.converters.GameStateConverter
import serialization.converters.PlayerSpecConverter
import serialization.data.RefereeStateDTO
import java.io.InputStreamReader
import java.util.*

object CleanupIntegrationTests {
    fun run() {
        val jsonReader = JsonReader(InputStreamReader(System.`in`, "UTF-8"))
        val gson = Gson()

        val playerSpec = gson.fromJson<List<List<String>>>(jsonReader, List::class.java)
        val refereeState = gson.fromJson<RefereeStateDTO>(jsonReader, RefereeStateDTO::class.java)

        val state = GameStateConverter.getRefereeStateFromDTO(refereeState, playerSpec.map { it[0] })
        val playerMechanisms = getPlayerMechanisms(playerSpec, state)

        val referee = TestableReferee(state)

        val endgame = referee.startGame(playerMechanisms, LinkedList())

        println(gson.toJson(listOf(endgame.winners, endgame.cheaters)))
    }

    private fun getPlayerMechanisms(specs: List<List<String>>, state: GameState): List<StrategyPlayerMechanism> {
        return specs.map { spec ->
            PlayerSpecConverter.getPlayerMechanism(spec)
        }
    }
}


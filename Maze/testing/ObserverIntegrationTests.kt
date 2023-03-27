package testing

import Common.GameState
import Players.PlayerMechanism
import Players.StrategyPlayerMechanism
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import serialization.converters.GameStateConverter
import serialization.data.RefereeStateDTO
import java.io.InputStreamReader
import java.util.LinkedList


object ObserverIntegrationTests {
    fun run() {
        val jsonReader = JsonReader(InputStreamReader(System.`in`, "UTF-8"))
        val gson = Gson()

        val playerSpec = gson.fromJson<List<List<String>>>(jsonReader, List::class.java)
        val refereeState = gson.fromJson<RefereeStateDTO>(jsonReader, RefereeStateDTO::class.java)

        val state = GameStateConverter.getRefereeStateFromDTO(refereeState, playerSpec.map { it[0] })
        val playerMechanisms = getPlayerMechanisms(playerSpec, state)
        val referee = TestableReferee(state)

        val endgameData = referee.playGame(state, playerMechanisms, LinkedList())

        val winners = endgameData.first

        println(gson.toJson(winners))
    }

    fun getPlayerMechanisms(specs: List<List<String>>, state: GameState): List<PlayerMechanism> {
        return specs.map {
            val name = it[0]
            val strategy = StrategyDesignation.valueOf(it[1])
            StrategyPlayerMechanism(
                name,
                strategy
            )
        }
    }
}




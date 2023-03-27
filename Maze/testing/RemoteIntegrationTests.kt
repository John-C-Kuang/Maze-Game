package testing

import Players.StrategyPlayerMechanism
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import serialization.converters.GameStateConverter
import serialization.converters.PlayerSpecConverter
import serialization.data.RefereeStateDTO
import java.io.InputStreamReader
import java.util.*
import kotlin.system.exitProcess

object RemoteIntegrationTests {

    fun run() {
        val jsonReader = JsonReader(InputStreamReader(System.`in`))
        val gson = Gson()

        val playerSpec = gson.fromJson<List<List<Any>>>(jsonReader, Any::class.java)
        val refereeState = gson.fromJson<RefereeStateDTO>(jsonReader, RefereeStateDTO::class.java)
        val playerNames = playerSpec.map { it[0].toString() }

        val state = GameStateConverter.getRefereeStateFromDTO(refereeState, playerNames)
        val playerMechanism: List<StrategyPlayerMechanism> = playerSpec.map { PlayerSpecConverter.getPlayerMechanism(it) }

        val referee = TestableReferee(state)

        val endgameData = referee.startGame(playerMechanism, LinkedList())

        println(Gson().toJson(listOf(endgameData.winners, endgameData.cheaters)))

        exitProcess(0)
    }
}
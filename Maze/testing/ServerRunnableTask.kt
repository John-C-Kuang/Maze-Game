package testing

import Common.GameState
import Common.board.Board
import Players.PlayerMechanism
import Referee.EndgameData
import Referee.Referee
import Server.Server
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import serialization.converters.GameStateConverter
import serialization.data.RefereeStateDTO
import java.io.InputStreamReader
import java.net.ServerSocket
import java.util.*
import kotlin.system.exitProcess

// cd to 9/
// tail -1 Other/ForStudents/1-in.json | ./xserver 12345

class ServerRunnableTask(
    portNumber: Int,
) {
    // TestableServer
    private var serverInstance: Server
    private val serverSocket = ServerSocket(portNumber)

    // Deserialize input
    init {
        val jsonReader = JsonReader(InputStreamReader(System.`in`))
        val gson = Gson()
        val refereeState = gson.fromJson<RefereeStateDTO>(jsonReader, RefereeStateDTO::class.java)
        serverInstance = TestableServer(refereeState, serverSocket)
    }

    fun run() {
        val endgameData = serverInstance.start()
        println(Gson().toJson(listOf(endgameData.winners, endgameData.cheaters)))
        exitProcess(0)
    }

}

class TestableServer(
    private val refereeState: RefereeStateDTO,
    serverSocket: ServerSocket
): Server(
    // This referee is _NOT_ used
    referee = ServerTestReferee(refereeState, mutableListOf()),
    serverSocket = serverSocket
) {

    override fun start(): EndgameData {
        val signedUpPlayers = signupPlayers()
        if (signedUpPlayers.size < MIN_PLAYERS) {
            return EndgameData(listOf(), listOf(), listOf())
        }
        val playerNames = mutableListOf<String>()
        signedUpPlayers.forEach { it
            playerNames.add(it.name)
        }
        playerNames
        val testableReferee = ServerTestReferee(refereeState, playerNames.reversed())
        return testableReferee.startGame(signedUpPlayers.reversed(), LinkedList())
    }
}

class ServerTestReferee(private val refereeState: RefereeStateDTO, private val names: List<String>): Referee() {
    override fun createStateFromChosenBoard(
        suggestedBoards: List<Board>,
        players: List<PlayerMechanism>
    ): GameState {
        return GameStateConverter.getRefereeStateFromDTO(refereeState, names)
    }

}
package testing

import Common.GameState
import Common.board.Board
import Common.board.Coordinates
import Players.PlayerMechanism
import Referee.EndgameData
import Referee.Referee
import Server.Server
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import serialization.converters.GameStateConverter
import serialization.data.CoordinateDTO
import serialization.data.RefereeState2DTO
import serialization.data.RefereeStateDTO
import java.io.InputStreamReader
import java.net.ServerSocket
import java.util.LinkedList
import kotlin.system.exitProcess

class ServerAdditionalGoalsRunnableTask(
    portNumber: Int,
) {
    // TestableServer
    private var serverInstance: Server
    private val serverSocket = ServerSocket(portNumber)

    // Deserialize input
    init {
        val jsonReader = JsonReader(InputStreamReader(System.`in`))
        val gson = Gson()
        val refereeState2 = gson.fromJson<RefereeState2DTO>(jsonReader, RefereeState2DTO::class.java)
        serverInstance = if (refereeState2.goals == null) {
            TestableServer2(refereeState2, serverSocket, LinkedList())
        } else {
            TestableServer2(refereeState2, serverSocket, refereeState2.goals)
        }
    }

    fun run() {
        val endgameData = serverInstance.start()
        println(Gson().toJson(listOf(endgameData.winners, endgameData.cheaters)))
        exitProcess(0)
    }

}

class TestableServer2(
    private val refereeState2: RefereeState2DTO,
    serverSocket: ServerSocket,
    private val goals: List<CoordinateDTO>
): Server(
    // This referee is _NOT_ used
    referee = ServerTestReferee2(refereeState2, mutableListOf()),
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
            println(it.name)
        }
        val testableReferee = ServerTestReferee2(refereeState2, playerNames.reversed())
        return testableReferee.startGame(signedUpPlayers.reversed(), LinkedList(goals.map {it.toCoordinate()}))
    }
}

class ServerTestReferee2(private val refereeState2: RefereeState2DTO, private val names: List<String>): Referee() {
    override fun createStateFromChosenBoard(
        suggestedBoards: List<Board>,
        players: List<PlayerMechanism>
    ): GameState {
        val refereeState = RefereeStateDTO(
            board = refereeState2.board,
            spare = refereeState2.spare,
            plmt = refereeState2.plmt,
            last = refereeState2.last
        )
        return GameStateConverter.getRefereeStateFromDTO(refereeState, names)
    }

}
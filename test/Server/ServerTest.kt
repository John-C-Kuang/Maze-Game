package Server

import Common.Skip
import Referee.RandomStateReferee
import com.google.gson.Gson
import com.google.gson.JsonPrimitive
import org.junit.After
import org.junit.Before
import org.junit.Test
import remote.RemoteConnection
import remote.TCPConnection
import serialization.converters.ActionConverter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.test.assertFalse


internal class ServerTest {



    private val referee = RandomStateReferee()

    private lateinit var serverSocket: ServerSocket

    private lateinit var server: Server
    private val ONE_AND_A_HALF = (3 * Server.WAITING_PERIOD_MILIS)/2
    private val TWICE_AND_SOME = 2 * Server.WAITING_PERIOD_MILIS + 100

    private var executor: ScheduledExecutorService? = null

    private val gson = Gson()


    @Before
    fun init() {
        serverSocket = ServerSocket(123)
        server = Server(referee, serverSocket)
    }

    @After
    fun teardown() {
        serverSocket.close()
        executor?.let { it.shutdownNow() }
    }

    @Test
    fun testPlayerSignupsEnoughOnFirstSession() {
        val clients = connectClients("Jesus", "Nick", "Other")
        clients.forEach { (name, conn) -> sendValidSkippingSequence(name, conn) }
        val endgameData = server.start()
        assert(endgameData.winners.size + endgameData.losers.size == 3)
        assert(endgameData.cheaters.isEmpty())
        clients.forEach{ (_, conn) -> conn.close()}

    }

    @Test
    fun testNoPlayersSignUpFirstSession() {
        executor = Executors.newScheduledThreadPool(2)

        val future = executor!!.submit(Callable {
            server.start()
        })

        executor!!.schedule(
            Callable { val client = connectClients("KingJ", "JZ")
                client.forEach { (name, conn) -> sendValidSkippingSequence(name, conn)
                }
        }, ONE_AND_A_HALF, TimeUnit.MILLISECONDS).get()

        val endgameData = future.get()

        assert(endgameData.winners.size + endgameData.losers.size == 2)
        assert(endgameData.cheaters.isEmpty())
    }

    @Test
    fun testPlayersSignUpAfterSecondWaitingPeriod() {
        executor = Executors.newScheduledThreadPool(2)

        val future = executor!!.submit(Callable {
            server.start()
        })

        executor?.schedule( {
            connectClients("Player1", "PLayer2", "Player3").forEach {
                (name, conn) -> sendValidSkippingSequence(name, conn)
            }
        }, TWICE_AND_SOME, TimeUnit.MILLISECONDS)?.get()

        val endgame = future.get()
        assert(endgame.winners.isEmpty())
        assert(endgame.losers.isEmpty())
        assert(endgame.cheaters.isEmpty())
    }

    @Test
    fun testNoOneSignsUp() {
        executor = Executors.newSingleThreadScheduledExecutor()
        val endgame = executor!!.submit(Callable {
            server.start()
        }).get()
        assert(endgame.winners.isEmpty())
        assert(endgame.losers.isEmpty())
        assert(endgame.cheaters.isEmpty())
    }

    @Test
    fun testMaxPlayersSignup() {
        val clients = connectClients(
            "Da", "Nick", "Shivers", "Rando1", "Jose", "Chengyi", "Suckstosuck"
        )
        clients.forEach { (name, conn) ->
            sendValidSkippingSequence(name, conn)
        }
        val endgame = server.start()
        assert(endgame.winners.size + endgame.losers.size == Server.MAX_PLAYERS)
        assert(endgame.cheaters.isEmpty())
    }

    @Test
    fun testInvalidNames() {
        val clients = connectClients(
            "", "Good", "Derek", "inv$@#alid", "waaaaaaay123456789toooooooooolong"
        )
        clients.forEach{ (name, conn) ->
            sendValidSkippingSequence(name, conn)
        }
        val endgame = server.start()
        val allPlayers = endgame.winners + endgame.losers + endgame.cheaters
        assert(allPlayers.size == 2)
        assertFalse(allPlayers.contains(""))
        assertFalse(allPlayers.contains("inv$@#alid"))
        assertFalse(allPlayers.contains("waaaaaaay123456789toooooooooolong"))
    }


    private fun sendValidSkippingSequence(name: String, connection: RemoteConnection) {
        connection.sendResponse(JsonPrimitive(name))
        connection.sendResponse(JsonPrimitive("void"))
        connection.sendResponse(ActionConverter.serializeChoice(Skip, gson))
        connection.sendResponse(JsonPrimitive("void"))
    }

    private fun connectClients(vararg name: String): Map<String, RemoteConnection> {
        return name.associate {
            Pair(it, TCPConnection(Socket("localhost", 123)))
        }
    }

}


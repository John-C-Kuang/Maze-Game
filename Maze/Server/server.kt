package Server

import Players.PlayerMechanism
import Referee.EndgameData
import Referee.Referee
import remote.ProxyPlayer
import remote.RemoteConnection
import remote.TCPConnection
import java.net.ServerSocket
import java.net.SocketTimeoutException
import java.util.LinkedList
import kotlin.jvm.Throws

/**
 * The server component for running games of Maze.
 *
 * Its interactions with clients goes as follows:
 * 1. Listen for connections on a predetermined, known ports
 * 2. Once a client connects and sends its name, it creates a player proxy. A name must be
 *    received within 2 seconds of the initial connection.
 * 3. If at least 2 players have joined after the initial waiting period of 20s or the max number (6) join,
 *      the game beings. If not enough players joined, the server enters waiting period again.
 *      If at most one player joins, the game does not begin and [[], []] is returned. Otherwise starts game.
 * 4. To start a game, it feeds players to the referee and lets it rip.
 */
open class Server(
    private val referee: Referee,
    private val serverSocket: ServerSocket = ServerSocket(PORT_NUMBER)
) {

    /**
     * Start up the server.
     *  - Sign up players
     *  - If enough players signed up, run a game
     */
    open fun start(): EndgameData {
        val signedUpPlayers = signupPlayers()
        if (signedUpPlayers.size < MIN_PLAYERS) {
            return EndgameData(listOf(), listOf(), listOf())
        }

        return referee.startGame(signedUpPlayers.reversed(), LinkedList())
    }


    /**
     * Signs up remote players, in a max of two waiting periods.
     */
    protected fun signupPlayers(): List<PlayerMechanism> {
        val playersAfterFirstPeriod = doOneWaitingPeriod()

        return if (playersAfterFirstPeriod.size < MIN_PLAYERS) {
            playersAfterFirstPeriod + doOneWaitingPeriod()
        } else playersAfterFirstPeriod
    }

    /**
     * Runs for WAITING_PERIOD_SECONDS. If the max number of players sign up, returns.
     * After receiving a successful TCP connection, waits for REMOTE_TIMEOUT_SECONDS to receive a
     * name.
     * If the name is valid, creates a player proxy for the player.
     */
    private fun doOneWaitingPeriod(): List<PlayerMechanism> {
        val players = mutableListOf<PlayerMechanism>()

        val endTime = System.currentTimeMillis() + WAITING_PERIOD_MILIS
        while (players.size < MAX_PLAYERS && System.currentTimeMillis() < endTime) {
            try {
                val tcpConnection =  nextTCPConnection(serverSocket, timeToEnd(endTime))

                val nameOptional = waitForPlayerName(tcpConnection)
                if (nameOptional != null && isValidName(nameOptional)) {
                    players.add(ProxyPlayer(nameOptional, tcpConnection))
                } else {
                    tcpConnection.close()
                }
            } catch (_: SocketTimeoutException) {

            }
        }

        return players
    }

    /**
     * Waits for a TCP connection.
     *
     * @throws SocketTimeoutException if waiting times out
     */
    @Throws(SocketTimeoutException::class)
    protected fun nextTCPConnection(serverSocket: ServerSocket, timeOut: Long): RemoteConnection {
        serverSocket.soTimeout = timeOut.toInt()

        val socket = serverSocket.accept()
        return TCPConnection(socket, RESPONSE_TIMEOUT_MILIS.toInt())
    }

    /**
     * Waits for the client to provide a name. Returns null if the connection times out.
     */
    private fun waitForPlayerName(tcpConnection: RemoteConnection): String? {
        return try {
            tcpConnection.readBlocking(String::class.java)
        } catch (_: SocketTimeoutException) {
            null
        }
    }

    /**
     * Is the provided name at least one character and at most one character long?
     * Does it match the regular expression?
     */
    private fun isValidName(name: String): Boolean {
        if (name.isEmpty() || name.length > 20) {
            return false
        }
        if (!Regex("^[a-zA-Z0-9]+$").matches(name)) {
            return false
        }
        return true
    }

    private fun timeToEnd(endTime: Long): Long {
        return endTime - System.currentTimeMillis()
    }

    companion object {
        const val MIN_PLAYERS = 2
        const val MAX_PLAYERS = 6
        const val PORT_NUMBER = 1000
        const val WAITING_PERIOD_SECONDS = 20L
        const val WAITING_PERIOD_MILIS = WAITING_PERIOD_SECONDS * 1000
        const val RESPONSE_TIMEOUT_SECONDS = 4L
        const val RESPONSE_TIMEOUT_MILIS = RESPONSE_TIMEOUT_SECONDS * 1000
    }
}
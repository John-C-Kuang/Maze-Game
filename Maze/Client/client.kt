package Client

import Players.PlayerMechanism
import com.google.gson.JsonPrimitive
import remote.ProxyReferee
import remote.RemoteConnection
import remote.TCPConnection
import java.io.IOException
import java.net.Socket

/**
* A client for the Maze.com distributed system.
*/
class Client(
    val player: PlayerMechanism,
    private val host: String,
    private val port: Int
) {

    /**
    * Connects to the server socket through TCP. Provides the player's name and creates a
    * ProxyReferee once the server has provided the initial game data.
    */
    fun start() {
        val remoteConnection = connect()

        remoteConnection.sendResponse(JsonPrimitive(player.name))

        ProxyReferee(player, remoteConnection).listen()

        remoteConnection.close()
    }

    /**
     * Waits until a connection with the server can be established. Creates a RemoteConnection.
     */
    private fun connect(): RemoteConnection {
        while (true) {
            try {
                val socket = Socket(host, port)
                return TCPConnection(socket)
            } catch (_: IOException) {
                println("Could not connect. Retrying in $WAITTIME_MILIS milliseconds.")
            }
            Thread.sleep(WAITTIME_MILIS)
        }
    }

    companion object {
        const val WAITTIME_MILIS = 500L
    }
}
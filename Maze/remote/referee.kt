package remote

import Players.PlayerMechanism
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSyntaxException
import serialization.converters.ActionConverter

/**
 * A client-side referee proxy. Handles communication between the client's player and
 * the remote player proxy.
 */
class ProxyReferee(
    private val player: PlayerMechanism,
    private val tcpConnection: RemoteConnection
) {

    private val gson = Gson()

    /**
     * Listens for server's method calls and executes them according to the player's instructions.
     */
    fun listen() {
        var isOver = false
        while (!isOver) {
            try {
                val receivedMethod = tcpConnection.readBlocking(MethodCall::class.java)
                playerRespond(receivedMethod)
                isOver = receivedMethod is WinMethod
            } catch (syntaxException: JsonSyntaxException) {
                println("Syntax exception occurred: ${syntaxException.message}")
                break
            } catch (e: MethodNotRecognizedException) {
                println(e.message)
                break
            } catch (e: Exception) {
                println("Unexpected exception occurred: ${e.message}")
                break
            }
        }
    }

    private fun playerRespond(receivedMessage: MethodCall) {
        val playerResponse = informPlayer(
            receivedMessage
        )
        tcpConnection.sendResponse(playerResponse)
    }

    /**
     * Relay the received method to the player and create its response.
     */
    private fun informPlayer(methodData: MethodCall): JsonElement {
        return when (methodData) {
            is SetupMethod -> {
                player.setupAndUpdateGoal(methodData.state, methodData.goal)
                VOID
            }
            is TakeTurnMethod -> {
                val action = player.takeTurn(methodData.state)
                ActionConverter.serializeChoice(action, gson)
            }
            is WinMethod -> {
                player.won(methodData.win)
                VOID
            }
        }
    }

    companion object {
        private val VOID = JsonPrimitive("void")
    }
}

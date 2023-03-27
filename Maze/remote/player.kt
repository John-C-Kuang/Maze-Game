package remote

import Common.Action
import Common.PublicGameState
import Common.board.Coordinates
import Common.tile.GameTile
import Players.PlayerMechanism
import com.google.gson.JsonElement
import serialization.converters.ActionConverter

class ProxyPlayer(
    override val name: String,
    private val tcpConnection: RemoteConnection
): PlayerMechanism {

    override fun proposeBoard0(rows: Int, columns: Int): Array<Array<GameTile>> {
        // TODO: implement propose board
        return arrayOf()
    }

    override fun setupAndUpdateGoal(state: PublicGameState?, goal: Coordinates) {
        sendMethod(SetupMethod(state, goal))
        receiveVoid()
    }

    override fun takeTurn(state: PublicGameState): Action {
        sendMethod(TakeTurnMethod(state))
        val answer = tcpConnection.readBlocking(JsonElement::class.java)
        return ActionConverter.deserializeChoice(answer, tcpConnection.gson)
    }

    override fun won(hasPlayerWon: Boolean) {
        sendMethod(WinMethod(hasPlayerWon))
        receiveVoid()
    }

    private fun sendMethod(methodCall: MethodCall) {
        tcpConnection.sendResponse(
            methodCall.toJson()
        )
    }

    private fun receiveVoid() {
        if (tcpConnection.readBlocking(String::class.java) != "void") {
            throw IllegalStateException("Player must return 'void'")
        }
    }
}
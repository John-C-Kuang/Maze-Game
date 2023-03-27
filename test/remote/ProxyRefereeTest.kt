package remote

import Common.TestData
import Common.board.Coordinates
import Players.StrategyPlayerMechanism
import com.google.gson.JsonPrimitive
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import serialization.converters.ActionConverter
import testing.StrategyDesignation

internal class ProxyRefereeTest {
    /**
     * Test cases:
     *  - Receive setup method
     *  - Receive take turn
     *  - Receive won
     *  - Receive bad method
     *  - Player messes up
     */

    @Mock
    private val remoteConnection = mock(RemoteConnection::class.java)

    private val playerMechanism = StrategyPlayerMechanism(
        "player1",
        StrategyDesignation.Riemann
    )

    private val gson = RemoteConnection.buildGson()

    private val proxyReferee = ProxyReferee(playerMechanism, remoteConnection)
    private val state = TestData.createGameState().toPublicState()
    private val goal = Coordinates.fromRowAndValue(1, 1)

    @Test
    fun testReceiveSetup() {
        `when`(remoteConnection.readBlocking(MethodCall::class.java))
            .thenReturn(SetupMethod(state, goal))
            .thenReturn(WinMethod(true))

        proxyReferee.listen()

        verify(remoteConnection, times(2)).sendResponse(JsonPrimitive("void"))
    }

    @Test
    fun testReceiveTakeTurn() {
        playerMechanism.setupAndUpdateGoal(state, goal)
        `when`(remoteConnection.readBlocking(MethodCall::class.java))
            .thenReturn(TakeTurnMethod(state))
            .thenReturn(WinMethod(false))

        proxyReferee.listen()

        verify(remoteConnection, times(1)).sendResponse(JsonPrimitive("void"))
        verify(remoteConnection, times(1)).sendResponse(
            ActionConverter.serializeChoice(
                playerMechanism.takeTurn(state), gson
            )
        )
    }

    @Test
    fun testReceiveWin() {
        `when`(remoteConnection.readBlocking(MethodCall::class.java))
            .thenReturn(WinMethod(true))

        proxyReferee.listen()
        verify(remoteConnection, times(1))
            .sendResponse(
                JsonPrimitive("void")
            )
    }


}
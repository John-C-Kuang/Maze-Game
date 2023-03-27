package remote

import Common.Skip
import Common.TestData
import Common.board.Coordinates
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.*
import serialization.converters.ActionConverter
import java.net.SocketTimeoutException
import kotlin.test.assertEquals

internal class ProxyPlayerTest {

    @Mock
    private val remoteConnection = mock(RemoteConnection::class.java)

    private val proxyPlayer = ProxyPlayer("Jesus", remoteConnection)

    private val publicState = TestData.createGameState().toPublicState()
    private val position = Coordinates.fromRowAndValue(1, 1)

    private val gson = RemoteConnection.buildGson()

    @Before
    fun setup() {
        `when`(remoteConnection.gson).thenReturn(gson)
    }

    @Test
    fun testSetupAndUpdateBoardInitial() {
        val setupInitial = SetupMethod(publicState, position)
        val setupInitialJSON = setupInitial.toJson()

        `when`(remoteConnection.readBlocking(String::class.java)).thenReturn("void")

        proxyPlayer.setupAndUpdateGoal(publicState, position)

        verify(remoteConnection, times(1)).sendResponse(setupInitialJSON)
    }

    @Test
    fun testSetupAndUpdateBoardNoVoid() {
        val setupInitial = SetupMethod(publicState, position)
        val setupInitialJSON = setupInitial.toJson()

        assertThrows<IllegalStateException>("Player must return 'void'") {
            proxyPlayer.setupAndUpdateGoal(publicState, position)
        }

        verify(remoteConnection, times(1)).sendResponse(setupInitialJSON)
    }

    @Test
    fun testTakeTurn() {
        val skipAction = ActionConverter.serializeChoice(Skip, gson)
        `when`(remoteConnection.readBlocking(String::class.java)).thenReturn("void")
        `when`(remoteConnection.readBlocking(JsonElement::class.java)).thenReturn(
            skipAction
        )

        val returnedTurn = proxyPlayer.takeTurn(publicState)

        verify(remoteConnection, times(1)).sendResponse(
            TakeTurnMethod(publicState).toJson()
        )

        assertEquals(Skip, returnedTurn)
    }

    @Test
    fun testTakeTurnPlayerDoesNotReturn() {
        `when`(remoteConnection.readBlocking(JsonElement::class.java))
            .thenThrow(SocketTimeoutException())

        assertThrows<SocketTimeoutException> {
            proxyPlayer.takeTurn(publicState)
        }


        verify(remoteConnection, times(1)).sendResponse(
            TakeTurnMethod(publicState).toJson()
        )
    }

    @Test
    fun testTakeTurnDoesNotReturnAction() {
        `when`(remoteConnection.readBlocking(JsonElement::class.java))
            .thenReturn(JsonPrimitive("hello"))

        assertThrows<IllegalArgumentException>("Invalid choice: \"hello\"") {
            proxyPlayer.takeTurn(publicState)
        }
    }

    @Test
    fun testWon() {
        val wonMethod = WinMethod(true).toJson()

        `when`(remoteConnection.readBlocking(JsonElement::class.java))
            .thenReturn(wonMethod)
        `when`(remoteConnection.readBlocking(String::class.java))
            .thenReturn("void")

        proxyPlayer.won(true)

        verify(remoteConnection, times(1)).sendResponse(wonMethod)


    }

}
package Client

import Common.board.ColumnPosition
import Common.board.Coordinates
import Common.board.RowPosition
import Players.StrategyPlayerMechanism
import com.google.gson.JsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import remote.MethodCall
import remote.RemoteConnection
import remote.SetupMethod
import testing.StrategyDesignation


internal class ClientTest {

    // Verifies PlayerMechanism has the correct strategy

    private var connection: RemoteConnection = Mockito.mock(RemoteConnection::class.java)
    private var dataSent = mutableListOf<String>()


    @BeforeEach
    fun init() {
        Mockito.reset(connection)
        dataSent = mutableListOf()
    }


    @Test
    fun `verify the number and order of the message received from TCP`() {
        val playerName = "Jose"
        val goal = Coordinates(RowPosition(9), ColumnPosition(7))
        Mockito.`when`(connection.sendResponse(JsonPrimitive(playerName))).then {
            dataSent.add("1. $playerName")
        }

        Mockito.`when`(connection.readBlocking(MethodCall::class.java)).then {
            dataSent.add("2. SetupMethod created.")
            return@then SetupMethod(null, goal)
        }

        Mockito.`when`(connection.sendResponse(JsonPrimitive("void"))).then {
            dataSent.add("3. Setup ACKed.")
            throw IllegalStateException("Ends here to prevent going into the loop in ProxyReferee")
        }

        val client = Client(
            player = StrategyPlayerMechanism(playerName, StrategyDesignation.Riemann),
            "localhost",
            1000
        )

        assertThrows<IllegalStateException> {
            client.start()
        }
        assertEquals(3, dataSent.size)
        assertEquals("1. $playerName", dataSent[0])
        assertEquals("2. SetupMethod created.", dataSent[1])
        assertEquals("3. Setup ACKed.", dataSent[2])

    }



}
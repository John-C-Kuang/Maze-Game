package testing

import Client.Client
import Players.PlayerMechanism
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import serialization.converters.PlayerSpecConverter
import java.io.InputStreamReader
import java.util.concurrent.Executors

// cd to 9/
// head -1 Other/ForStudents/1-in.json | ./xclients 12345

class ClientsRunnableTask (
    private val portNumber: Int,
    private var ipAddress: String?
) {

    var playerMechanisms: List<PlayerMechanism>

    init {
        ipAddress = ipAddress?: "127.0.0.1"
        println("IP: $ipAddress")
        println("Port: $portNumber")
        val jsonReader = JsonReader(InputStreamReader(System.`in`))
        val gson = Gson()
        val playerSpec = gson.fromJson<List<List<Any>>>(jsonReader, Any::class.java)
        playerMechanisms = playerSpec.map { PlayerSpecConverter.getPlayerMechanism(it) }
    }

    fun run() {
        var count = 1
        playerMechanisms.forEach {
            it
            val client = Client(
                player = it,
                host = ipAddress!!,
                port = portNumber
            )
            val executor = Executors.newSingleThreadExecutor()
            executor.submit { client.start() }
            println("Client $count started")
            count++
            Thread.sleep(WAIT_INTERVAL)
        }
    }

    companion object {
        const val WAIT_INTERVAL = 3000L
    }
}
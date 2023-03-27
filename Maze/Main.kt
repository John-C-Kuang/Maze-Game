
import Client.Client
import Client.javafx.CommandLineRefereeApp
import Players.StrategyPlayerMechanism
import Referee.RandomStateReferee
import Server.Server
import javafx.RandomStateServerApplication
import javafx.application.Application.launch
import serialization.converters.EndGameConverter
import testing.*

fun main(args: Array<String>) {
    when (args[0]) {
        "xgames" -> ObserverIntegrationTests.run()
        "xgames-with-observer" -> launch(CommandLineRefereeApp::class.java)
        "xbad" -> CleanupIntegrationTests.run()
        "xbad2" -> RemoteIntegrationTests.run()
        "runServerObserver" -> launch(RandomStateServerApplication::class.java)
        "runServer" -> {println(EndGameConverter.serialize(
            Server(RandomStateReferee()).start()
        )); println("done")}
        "runClient" -> Client(StrategyPlayerMechanism(args[1], StrategyDesignation.Riemann), "localhost", 1000).start()
        "xserver" -> ServerRunnableTask(args[1].toInt()).run()
        "xclients" -> ClientsRunnableTask(args[1].toInt(), if (args.size > 2) args[2] else null).run()
        "xserver2" -> ServerAdditionalGoalsRunnableTask(args[1].toInt()).run()
    }
}
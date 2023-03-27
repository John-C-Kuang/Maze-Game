package Client.javafx

import Client.LocalStateObserver
import Referee.EndgameData
import Referee.ObserverMechanism
import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.FileChooser
import javafx.stage.Stage
import serialization.converters.EndGameConverter
import java.util.concurrent.Callable
import java.util.concurrent.Executors


/**
 * To create a Graphical User Interface that observers a game being executed by a referee.
 * Applications will provide users with a "next" button to display the next state available, and
 * a "save" button to save the serialized state into a specified file.
 *
 * Implementing classes must specify how to construct a game and the player mechanisms.
 */
abstract class RefereeObserverApplication: Application() {
    private val SCREEN_WIDTH = 1000.0
    private val SCREEN_HEIGHT = 750.0

    private val executor = Executors.newSingleThreadExecutor()

    abstract fun startGame(observer: ObserverMechanism): EndgameData

    override fun start(primaryStage: Stage?) {
        val observer = setup(primaryStage)

        val future = executor.submit(Callable { startGame(observer) })
        println(EndGameConverter.serialize(
            future.get()
        ))
    }

    private fun setup(primaryStage: Stage?): LocalStateObserver {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/JavaFXObserver.fxml"))
        val parent = fxmlLoader.load<Parent>()
        val controller = fxmlLoader.getController<LocalStateObserver>()

        primaryStage?.run {
            val fileChooser = FileChooser()
            controller.saveButton.onAction = EventHandler {
                fileChooser.extensionFilters.addAll(FileChooser.ExtensionFilter("All Files", "*.*"))
                val file = fileChooser.showSaveDialog(this)
                file?.let {
                    controller.save(file)
                }
            }
            scene = Scene(parent, SCREEN_WIDTH, SCREEN_HEIGHT)
            show()
        }

        return controller
    }

    override fun stop() {
        executor.shutdownNow()
    }
}





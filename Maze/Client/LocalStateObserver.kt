package Client

import Client.javafx.renderGameState
import Common.GameState
import Common.rest
import Referee.ObserverMechanism
import com.google.gson.GsonBuilder
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import serialization.converters.GameStateConverter
import serialization.data.RefereeStateDTO
import java.io.File
import java.io.FileWriter

/**
 * A JavaFX implementation of an observer that observes a local game.
 */
class LocalStateObserver: ObserverMechanism {

    @FXML
    lateinit var board: VBox
    @FXML
    lateinit var parent: HBox
    @FXML
    lateinit var controlPanel: VBox
    @FXML
    lateinit var spareTile: StackPane
    @FXML
    lateinit var saveButton: Button
    @FXML
    lateinit var nextButton: Button
    private var gamestates = listOf<GameState>()
    private var isGameOver = false

    /**
     * Adds a new state to be displayed.
     */
    override fun updateState(newState: GameState) {
        gamestates = gamestates + listOf(newState)
    }

    override fun gameOver() {
        isGameOver = true
    }

    /**
     * Retrieves the latest state, displays it graphically.
     */
    fun next() {
        if (gamestates.isNotEmpty()) {
            val first = gamestates.first()
            gamestates = gamestates.rest()
            displayState(first)
        }

        if (isGameOver && gamestates.isEmpty()) {
            displayGameOver()
        }
    }

    /**
     * Displays the provided state.
     */
    fun displayState(gameState: GameState) {
        val (board, spareTile) = renderGameState(gameState)
        this.board.children.clear()
        this.board.children.addAll(board.children)
        this.spareTile.children.clear()
        this.spareTile.children.addAll(spareTile.children)
    }

    fun displayGameOver() {
        this.controlPanel.children.add(Text("Game Over!"))
        this.nextButton.isDisable = true
    }

    /**
     * Serializes the currently displayed state and saves it to the specified file.
     */
    fun save(file: File) {
        if (gamestates.isNotEmpty()) {
            val currentState = gamestates.first()

            val serializedState = GameStateConverter.serializeGameState(currentState)

            writeStateToFile(file, serializedState)
        }
    }

    private fun writeStateToFile(file: File, serializedState: RefereeStateDTO) {
        val fileWriter = FileWriter(file)
        val gson = GsonBuilder()
            .serializeNulls()
            .create()
        fileWriter.write(gson.toJson(serializedState))
        fileWriter.close()
    }
}
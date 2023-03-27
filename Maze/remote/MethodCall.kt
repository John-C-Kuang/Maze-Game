package remote

import Common.PublicGameState
import Common.board.Coordinates
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import serialization.converters.CoordinateConverter
import serialization.converters.PublicGameStateConverter


sealed interface MethodCall {
    val name: String

    fun toJson(): JsonElement {
        val gson = GsonBuilder().serializeNulls().create()
        val array = JsonArray()
        array.add(name)
        array.add(argsToJsonArray(gson))
        return array
    }

    fun argsToJsonArray(gson: Gson): JsonArray
}

data class SetupMethod(
    val state: PublicGameState?,
    val goal: Coordinates
): MethodCall {
    override val name: String = "setup"

    override fun argsToJsonArray(gson: Gson): JsonArray {
        val array = JsonArray()
        array.add(gson.toJsonTree(
            state?.let {  PublicGameStateConverter.serializeGameState(it) } ?: false
        ))
        array.add(gson.toJsonTree(CoordinateConverter.toDto(goal)))
        return array
    }
}

data class TakeTurnMethod(
    val state: PublicGameState
): MethodCall {

    override val name = "take-turn"

    override fun argsToJsonArray(gson: Gson): JsonArray {
        val array = JsonArray()
        array.add(gson.toJsonTree(PublicGameStateConverter.serializeGameState(state)))
        return array
    }
}

data class WinMethod(
    val win: Boolean
): MethodCall {

    override fun argsToJsonArray(gson: Gson): JsonArray {
        val array = JsonArray()
        array.add(win)
        return array
    }

    override val name = "win"
}

class MethodNotRecognizedException(message: String) : IllegalArgumentException(message)
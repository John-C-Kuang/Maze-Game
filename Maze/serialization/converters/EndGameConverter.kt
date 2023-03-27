package serialization.converters

import Referee.EndgameData
import com.google.gson.Gson

object EndGameConverter {

    fun serialize(endgameData: EndgameData): String {
        val gson = Gson()
        return gson.toJson(listOf(
            endgameData.winners,
            endgameData.cheaters
        ))
    }
}
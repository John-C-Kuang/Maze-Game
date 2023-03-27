package serialization.converters

import com.google.gson.*
import remote.*
import serialization.data.CoordinateDTO
import serialization.data.StateDTO
import java.lang.reflect.Type

class MethodCallDeSerializer: JsonDeserializer<MethodCall> {

    override fun deserialize(json: JsonElement?, type: Type?, context: JsonDeserializationContext?): MethodCall {
        val (mname, args) = validateJson(json)
        return when (mname) {
            "setup" -> deserializeSetup(args)
            "take-turn" -> deserializeTakeTurn(args)
            "win" -> deserializeWon(args)
            else -> throw MethodNotRecognizedException("Invalid Method name: $mname.")
        }
    }

    private fun validateJson(json: JsonElement?): Pair<String, JsonArray> {
        if (json == null || !json.isJsonArray) {
            throw MethodNotRecognizedException("MethodCall must be a json array, found: $json.")
        }
        val jsonArray = json.asJsonArray
        val mName = jsonArray.get(0)
        val args = jsonArray.get(1)
        if (!mName.isJsonPrimitive || !mName.asJsonPrimitive.isString) {
            throw MethodNotRecognizedException("MName must be a String, found $mName.")
        }
        if (!args.isJsonArray) {
            throw MethodNotRecognizedException("Arguments must be a JSOnArray, found $args.")
        }
        return Pair(mName.asJsonPrimitive.asString, args.asJsonArray)

    }

    private fun deserializeSetup(args: JsonArray): MethodCall {
        val first = args.get(0).let { it ->
            if (it.isJsonPrimitive && it.asJsonPrimitive.isBoolean) {
                null
            } else {
                PublicGameStateConverter.getPublicGameStateFromDTO(
                    Gson().fromJson(it, StateDTO::class.java),
                    "dummy"
                )
            }
        }
        val second = CoordinateConverter.coordinateFromDTO(
            Gson().fromJson(args.get(1), CoordinateDTO::class.java)
        )
        return SetupMethod(first, second)
    }

    private fun deserializeTakeTurn(args: JsonArray): MethodCall {
        return TakeTurnMethod(
            PublicGameStateConverter.getPublicGameStateFromDTO(
                Gson().fromJson(args.get(0), StateDTO::class.java),
                "dummy"
            )
        )
    }

    private fun deserializeWon(args: JsonArray): MethodCall {
        return WinMethod(args.get(0).asBoolean)
    }
}
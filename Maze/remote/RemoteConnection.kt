package remote

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import serialization.converters.MethodCallDeSerializer
import java.net.SocketTimeoutException

/**
 * To create remote connection from server to clients.
 */
interface RemoteConnection {

    val gson: Gson
        get() = buildGson()

    /**
     * Reads an element of the desired type.
     * This operation should block until it either returns a value or times out.
     */
    @kotlin.jvm.Throws(SocketTimeoutException::class)
    fun <T> readBlocking(clazz: Class<T>): T

    fun hasNext(): Boolean

    fun sendResponse(response: JsonElement)

    fun close()

    companion object {
        fun buildGson(): Gson {
            return GsonBuilder()
                .registerTypeAdapter(MethodCall::class.java, MethodCallDeSerializer())
                .serializeNulls()
                .create()
        }
    }
}
package remote

import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.JsonReader
import java.io.BufferedOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.net.SocketTimeoutException


/**
 * To handle TCP messages on the provided socket.
 *
 * If timeout is non-zero, all operations will throw SocketTimeoutException
 * on timeout; otherwise operations will block forever and ever.
 */
class TCPConnection(
    private val socket: Socket,
    timeout: Int = 0
): RemoteConnection {

    init {
        if (timeout != 0) {
            socket.soTimeout = timeout
        }
    }

    private val jsonReader = JsonReader(InputStreamReader(socket.getInputStream()))
    private val writer = OutputStreamWriter(BufferedOutputStream(socket.getOutputStream()))

    /**
     * Tries reading an object of the desired type from the connection.
     *
     * This operation is blocking and will throw exception on timeout.
     */
    override fun <T> readBlocking(clazz: Class<T>): T {
        try {
            return gson.fromJson(jsonReader, clazz)
        } catch (e: JsonSyntaxException) {
            if (e.cause?.let {it is SocketTimeoutException} == true) {
                throw SocketTimeoutException("No input received.\n$e")
            } else {
                throw e
            }
        }
    }

    override fun hasNext(): Boolean {
        return jsonReader.hasNext()
    }

    override fun sendResponse(response: JsonElement) {
        gson.toJson(response, writer)
        writer.flush()
    }

    override fun close() {
        socket.close()
    }


}

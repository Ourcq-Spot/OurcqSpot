import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

fun sendMessageToServer(message: String, onResponse: (String?) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val socket = Socket("192.168.0.28", 49151)
            val writer = PrintWriter(socket.getOutputStream(), true)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            writer.println(message)
            val response = reader.readLine()

            withContext(Dispatchers.Main) {
                onResponse(response)
            }

            reader.close()
            writer.close()
            socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onResponse(null)
            }
        }
    }
}

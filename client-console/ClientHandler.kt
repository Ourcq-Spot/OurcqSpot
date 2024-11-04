import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel
import java.util.*

/**
 * Class that manages the client side and its connection to the server.
 * Allows writing to and reading from the server once connection finished.
 * 
 * (Also implements the Runnable interface, so can be inserted in Thread() constructor or in ExecutorService.submit() to use the run() method)
 */
class ClientHandler private constructor(initClient: Boolean = false) : Runnable {

    /**
     * Hostname or IP address of the server.
     */
    companion object {
        private const val DEFAULT_BYTEBUFFER_MESSAGE_LENGTH: Int = 1024
        public var SERVER_HOST: String = "91.175.109.179"
        public var SERVER_PORT: Int = 49152
        private var _instance: ClientHandler = ClientHandler()
        /**
         * Retrives the ClientHandler object (Singleton in _instance property).
         *  (Creates it if not already created)
         * 
         * @return Single instance (Singleton) of the ClientHandler
         */
        public fun getInstance(): ClientHandler = synchronized(this){
            return _instance
        }
    }

    /**
     * "Selector" that handles the way to interact with the server once connected.
     *  (kinda listens to read & write events)
     */
    private var selector: Selector? = null

    /**
     * Some timeout that does almost nothing (used in selector.select(...)).
     */
    private val SELECTOR_TIMEOUT: Long = 1000
    
    /**
     * Storage capacity for a ByteBuffer (used in this.byteBuffer).
     */
    private val byteBuffer: ByteBuffer = ByteBuffer.allocate(DEFAULT_BYTEBUFFER_MESSAGE_LENGTH)

    
    /**
     * Constructor of the ClientHandler object that immediately uses the initClient() method.
     *  (Depends on initClient that might be passed as param)
     */
    init {
        if (initClient) {
            initClient()
        }
    }

    /**
     * Default way to init the client by calling the initClient() method setting the runClient parameter to true.
     */
    public fun initClient() {
        initClient(true)
    }

    /**
     * Inits the client.
     *  (In fact just checks if the selector isn't already set (==connection active)
     *  and automatically uses runClient() if needed)
     * 
     * @param runClient Decides if we call runClient() method or not
     */
    public fun initClient(runClient: Boolean) {
        println("> ClientHandler.initClient()")

        if (selector != null) return // Checks if connection is already active

        println("Preparing connection to server...")

        if (runClient) {
            runClient()
        }
    }

    /**
     * Initiates the connection between the client and the server with sockets.
     */
    fun runClient() {
        println("> ClientHandler.runClient()")

        try {
            selector = Selector.open()
            val socketChannel = SocketChannel.open()
            socketChannel.configureBlocking(false)

            // Register connection in Selector
            socketChannel.register(selector, SelectionKey.OP_CONNECT)
            socketChannel.connect(InetSocketAddress(SERVER_HOST, SERVER_PORT))
            println("Connected to server [$SERVER_HOST:$SERVER_PORT]")

            run()
        } catch (e: IOException) {
            println("[IOException]")
        } finally {
            // Accessed when application closes (including by force I think)
            closeConnection()
        }
    }

    /**
     * When the ClientHandler is used in a Thread as a Runnable, it enters an infinite loop handling events between the client and the server.
     */
    override fun run() {
        println("> ClientHandler.run()")

        try {
            while (!Thread.interrupted()) {
                selector?.select(SELECTOR_TIMEOUT)

                // Gets all events
                val selectionKeys = selector?.selectedKeys()?.iterator()

                // Handles each event
                while (selectionKeys?.hasNext() == true) {
                    val selectionKey = selectionKeys.next()
                    selectionKeys.remove()

                    if (!selectionKey.isValid) { // If the client closed the connection...
                        continue // we don't want events that aren't valid!
                    }

                    if (selectionKey.isConnectable) {
                        connectToServer(selectionKey)
                    }
                    if (selectionKey.isWritable) {
                        writeToServer(selectionKey)
                    }
                    if (selectionKey.isReadable) {
                        readFromServer(selectionKey)
                    }
                }
            }
        } catch (e: IOException) {
            println("[IOException] Probably with .select() ?")
        }
    }

    /**
     * Attempts to connect to the server.
     *  (normally if any was found and server is ready with OP_ACCEPT)
     * 
     * @param selectionKey Key from the selector that contains the data about the connection
     */
    fun connectToServer(selectionKey: SelectionKey) {
        println("Connecting to server...")

        try {
            // We use the SocketChannel kept in the memory of the SelectionKey
            val sc = selectionKey.channel() as SocketChannel
            if (sc.isConnectionPending) { // If connection is not yet accepted by the server...
                sc.finishConnect()
                println("Connection successful.")
            }
            sc.configureBlocking(false) // Non-blocking sockets enable connections from multiple clients at the same time!
            selectionKey.interestOps(SelectionKey.OP_WRITE)
            println(" >> Ready to WRITE to server << ")
        } catch (e: IOException) {
            println("[IOException] Failed to finish connection with server.")
        }
    }

    /**
     * Attempts to read a message from the server.
     *  (normally should be called if a SelectionKey for reading (event) is received (== server wrote something))
     * 
     * @param selectionKey Key from the selector that contains the data about the connection
     * @return Output that was sent by the server, that can therefore be used outside of the function!
     */
    fun readFromServer(selectionKey: SelectionKey): String {
        var output = ""

        try {
            val sc = selectionKey.channel() as SocketChannel
            byteBuffer.clear() // Prepares the buffer for a new message
            val messageLength: Int
            try {
                messageLength = sc.read(byteBuffer) // Reads the content that was received
            } catch (e: IOException) {
                println("[${e.javaClass}] Problem while trying to read through the SocketChannel, closing connection.")
                selectionKey.cancel()
                sc.close()
                return output
            }
            if (messageLength == -1) {
                println("Nothing was read from server (SocketChannel.read()), closing connection.")
                sc.close()
                selectionKey.cancel()
                return output
            }
            byteBuffer.flip()
            val buff = ByteArray(DEFAULT_BYTEBUFFER_MESSAGE_LENGTH)
            byteBuffer.get(buff, 0, messageLength) // Stores the message as bytes (because transferred as bytes in sockets)

            output = String(buff) // Gets the output as a String message

            println("[MESSAGE FROM SERVER]")
            println(" >> \"$output\"")

            selectionKey.interestOps(SelectionKey.OP_WRITE) // Prepares to write to the server in response (not mandatory if don't want infinite exchanges)

        } catch (e: IOException) {
            println("[IOException] Could not connect to read from the server!")
            selectionKey.cancel()
        }

        return output
    }

    /**
     * By default asks the user to input some message if none was passed as a parameter.
     * 
     * @param selectionKey Key from the selector that contains the data about the connection
     */
    fun writeToServer(selectionKey: SelectionKey) {
        writeWithTextInput(selectionKey)
    }

    /**
     * Attempts to send a String message to the server.
     * 
     * @param selectionKey Key from the selector that contains the data about the connection
     * @param text Message to send to the server
     */
    fun writeToServer(selectionKey: SelectionKey, text: String) {
        try {
            println("WRITE TARGET: server")
            println("MESSAGE: \"$text\"")

            val sc = selectionKey.channel() as SocketChannel
            sc.write(ByteBuffer.wrap(text.toByteArray())) // Transfers the message as bytes in a ByteBuffer through the socket
            println("Message sent to server.")

            selectionKey.interestOps(SelectionKey.OP_READ) // Prepares a future response from the server
            println(" >> Ready to READ from server << ")

        } catch (e: IOException) {
            println("[IOException] Could not write to the server!")
            selectionKey.cancel()
        }
    }

    /**
     * Attempts to close the connection with the server.
     */
    fun closeConnection() {
        println("Closing connection to server...")
        selector?.let {
            try {
                it.close()
            } catch (e: IOException) {
                println("[IOException] Connection to server could not be closed.")
            }
        }
    }

    /**
     * Asks the user to input some message manually in the console.
     * 
     * @param selectionKey Key from the selector that contains the data about the connection
     */
    fun writeWithTextInput(selectionKey: SelectionKey) {
        val scanner = Scanner(System.`in`) // Thing that allows reading in the console
        println("[ENTER YOUR REQUEST]")
        val text = scanner.nextLine() // Retrieves the input

        writeToServer(selectionKey, text) // Calls the method that attempts to send the message to the server
    }
}


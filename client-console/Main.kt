import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.lang.IndexOutOfBoundsException

/**
 * ClientHandler object (singleton?) that should be initialized later.
 */
private lateinit var clientHandler: ClientHandler

/**
 * Entry point of the program (function that is called).
 *  (https://stackoverflow.com/questions/890966/what-is-the-string-args-parameter-in-the-main-method)
 * 
 * @param args List of arguments that can be passed when starting the program, but not used here
 */
fun main(args: Array<String>) {
    println("> main()")

    try {
        ClientHandler.SERVER_HOST = args[0]
    } catch (e: IndexOutOfBoundsException) {
        println("No IP set (args[0])")
    }
    try {
        ClientHandler.SERVER_PORT = args[1].toInt()
    } catch (e: IndexOutOfBoundsException) {
        println("No PORT set (args[0])")
    } catch (e: NumberFormatException) {
        println("Could not read PORT as an Int")
    }

    //println(ClientHandler.SERVER_HOST)
    //println(ClientHandler.SERVER_PORT)


    try {
        // Getting the ClientHandler as an object (in the "clientHandler" property)
        clientHandler = ClientHandler.getInstance()
        clientHandler.initClient()


        println("[Creating a single Thread for the ClientHandler to do its things]")
        val future: Future<*> = Executors.newSingleThreadExecutor()
            .submit(clientHandler)
        // Blocking the finally (that would close connection) until program ends
        while (!future.isDone) {} // [while the connection isn't unset]

    } finally {
        try {
            println("Closing connection from main()...")
            clientHandler.closeConnection()
        } catch (e: Exception) {
            System.err.println("[${e::class}] Connection could not be closed in main()")
        }
    }
    
}


        /*val scanner = Scanner(System.`in`) // Thing that allows reading in the console
        println("[ENTER THE SERVER'S PUBLIC IP ADRESS]")
        val SERVER_HOST = scanner.nextLine()
        println("[ENTER THE SERVER'S PORT]")
        val SERVER_PORT = scanner.nextLine()*/
        /*ClientHandler.SERVER_HOST = SERVER_HOST
        ClientHandler.SERVER_PORT = SERVER_PORT*/
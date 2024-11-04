import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class that manages the server side and its connection to any client.
 * Allows writing to and reading from the clients once connection finished.
 * 
 * (Also implements the Runnable interface, so can be inserted in Thread() constructor or in ExecutorService.submit() to use the run() method)
 */
public class ServerHandler implements Runnable{


    /**
     * Storage capacity for a ByteBuffer (used in this.byteBuffer).
     */
    private static final int DEFAULT_BYTEBUFFER_MESSAGE_LENGTH = 1024;
    /**
     * A container for data of byte type. 
     */
    private final ByteBuffer byteBuffer = ByteBuffer.allocate(ServerHandler.DEFAULT_BYTEBUFFER_MESSAGE_LENGTH);

    /**
     * Hostname or IP adress of the server (this machine!).
     *  (should be dynamic and independant of the machine)
     */
    //private static final String SERVER_HOST="localhost";
    //private static final String SERVER_HOST="127.0.0.1";
    //private static final String SERVER_HOST="192.168.56.1";
    public static String SERVER_HOST = "192.168.1.26";
    //private static final String SERVER_HOST="192.168.1.26";
    //private static final String SERVER_HOST="91.175.109.179";
    /**
     * Port that the server should be listening (for socket connections). 
     */
    public static Integer SERVER_PORT=49152;
    /**
     * Maximum number of Threads to handle read/write Runnable tasks. (see "threadPool" property and "run()" method)
     */
    private static final Integer MAX_THREADS=8;
    /**
     * "Thing" that contains multiple Threads, and that allows to submit Runnable tasks if at least one Thread isn't used.
     *  (If all Threads of the pool are used, the tasks are not affected until a Thread becomes available)
     */
    private final ExecutorService threadPool = Executors.newFixedThreadPool(ServerHandler.MAX_THREADS);
    
    /**
     * Object (singleton instance) of that same ServerHandler class.
     */
    private static ServerHandler _instance;
    /**
     * Used to manage the server and the connection to clients I think?
     */
    private ServerSocketChannel serverSocketChannel;
    /**
     * Selector that manages every connection to clients and their events (read/write).
     */
    private volatile Selector selector;
    /**
     * Some timeout that does almost nothing (used in selector.select(...)).
     */
    private final Integer SELECTOR_TIMEOUT = 2000;



    /**
     * Default constructor of the ServerHandler object (defines initServer as true).
     */
    public ServerHandler() {
        this(true);
    }
    /**
     * Constructor of the ServerHandler object that immediately uses the initServer() method.
     * 
     * @param initServer Defines if we automatically call initServer()
     */
    public ServerHandler(boolean initServer) {
        if (initServer) {
            this.initServer();
        }
    }



    /**
     * Retrives the ServerHandler object (Singleton in _instance property).
     *  (Creates it if not already created)
     * 
     * @return Single instance (Singleton) of the ServerHandler
     */
    public static ServerHandler getInstance() {
        if (ServerHandler._instance == null) {
            ServerHandler._instance = new ServerHandler();
        }
        return ServerHandler._instance;
    }




    /**
     * When the ServerHandler is used in a Thread as a Runnable, it enters an infinite loop handling events between the server and clients.
     */
    @Override
    public void run() {
        System.out.println("> ServerHandler.run()");

        try {

            System.out.println("[NOW ACCEPTING CONNECTIONS]");
            while (!Thread.currentThread().isInterrupted()) {
                // System.out.println("Checking requests...");

                // Continuously waits for an event
                this.selector.select(this.SELECTOR_TIMEOUT);

                // Gets all events
                Iterator<SelectionKey> selectionKeys = this.selector.selectedKeys().iterator();

                // Handles each event
                while (selectionKeys.hasNext()) {
                    // System.out.println("[event found in selection keys...]");
                    SelectionKey selectionKey = selectionKeys.next();
                    selectionKeys.remove();

                    if (!selectionKey.isValid()) { // If the client closed the connection...
                        continue; // we don't want events that aren't valid!
                    }

                    if (selectionKey.isAcceptable()) { // A client wants to connect!
                        // We might want to associate info to him (id, ...)
                        ClientAttachment clientAttachment = new ClientAttachment();
                        selectionKey.attach(clientAttachment);
                        this.acceptClientConnection(selectionKey); // let's tell him it's okay to get in
                    }

                    try {
                        // Depending on if the client connection is already processing something...
                        ClientAttachment clientAttachment = (ClientAttachment) selectionKey.attachment();
                        if(clientAttachment!=null){
                            if (!clientAttachment.isProcessing()) {
                                // ...we might be able to process some new read/write message!
                                if (
                                    selectionKey.isWritable()
                                    || selectionKey.isReadable()
                                ) {
                                    // Now the client connection is officially processing something (either write or read)
                                    clientAttachment.setProcessing();

                                    // The task to read/write
                                    Runnable task = () -> {
                                        try {
                                            if (selectionKey.isWritable()) {
                                                this.writeToClient(selectionKey);
                                            }
                                            if (selectionKey.isReadable()) {
                                                this.readFromClient(selectionKey);
                                            }
                                        } finally {
                                            // And now it is officially not processing anymore
                                            clientAttachment.unsetProcessing();
                                        }
                                    };
                                    // Now we do the Runnable task in another Thread (performance!)
                                    /*Future<?> future = */this.threadPool.submit(task);
                                }
                            }
                        }else{
                            System.out.println("//" + Thread.currentThread().getName() + "// attachment => NULL!!");
                        }
                    } catch (Exception e) {
                        System.out.println("[" + e.getClass() + "] selectionKey.attachment() might not be of type ClientAttachment...");
                        //e.printStackTrace();
                        return;
                    }

                }
            }
        } catch (IOException e) {
            System.out.println("[IOException] Server connection could run and be opened to connections.");
        } finally {
            // Now, even if the program crashes, the connection should close.
            this.closeServerConnection();
        }
    }

    /**
     * Default way to init the server by calling the initServer() method setting the runServer parameter to true.
     */
    private void initServer() {
        initServer(true);
    }
    /**
     * Inits the server.
     *  (In fact just checks if the selector (or the serverSocketChannel) isn't already set (==connection active)
     *  and automatically uses runServer() if needed)
     * 
     * @param runServer Decides if we call runServer() method or not
     */
    public void initServer(boolean runServer) {
        System.out.println("> ServerHandler.initServer()");

        // Check if server not already running
        if (this.selector != null) return;
        if (this.serverSocketChannel != null) return;

        try {
            System.out.println(
                "Initializing the ServerSocketChannel..." +
                " [blocking => true, hostname => " + ServerHandler.SERVER_HOST +
                ", port => " + ServerHandler.SERVER_PORT + "]"
            );
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.configureBlocking(false);
            this.serverSocketChannel.socket().bind(
                new InetSocketAddress(
                    ServerHandler.SERVER_HOST,
                    ServerHandler.SERVER_PORT
                )
            );

            System.out.println("Registering the serverSocketChannel as accepting connections...");
            this.selector = Selector.open();
            this.serverSocketChannel.register(
                this.selector,
                SelectionKey.OP_ACCEPT // Accept connections from clients for this "ServerSocketChannel", which is what's used here just for accepting connections
            );

            if (runServer) {
                Thread thread = new Thread(this);
                thread.start();
                //Future<?> future = this.threadPool.submit(this); // ------ for later => IF WE DO THIS WE CAN RE-RUN AUTOMATICALLY IN CASE THE SERVER FAILS!!
            }
        } catch (IOException e) {
            System.out.println("[IOException] Server connection could not be initialized.");
        }
    }

    /**
     * Attempts to accept a connection from a client app.
     *  (if any client tried to use a socket between itself and this server)
     * 
     * @param selectionKey Key from the selector that contains the data about the connection
     */
    public void acceptClientConnection(SelectionKey selectionKey) {
        System.out.println("> Serverhandler.acceptClientConnection()");

        try {
            ServerSocketChannel ssc = (ServerSocketChannel) selectionKey.channel(); // Get the ServerSocketChannel (if it was one, else we catch an Exception) out of the SelectionKey
            SocketChannel sc = ssc.accept(); // We create the real Socket between this client and the server by accepting the connection
            System.out.println("[CONNECTION ACCEPTED]");
            sc.configureBlocking(false);


            //selectionKey.interestOps(SelectionKey.OP_READ);
            
            ClientAttachment clientAttachment = (ClientAttachment) selectionKey.attachment();
            // We register that created Socket in the selector to reuse it later (and handle updates for read/write)
            sc.register(
                this.selector,
                SelectionKey.OP_READ,
                clientAttachment
            );

            
            System.err.println(" >> Ready to READ from client[" + clientAttachment.getConnectionId() + "] << ");
        } catch (IOException e) {
            System.out.println("[IOException] Connection to client failed.");
        }
    }
    
    /**
     * Attempts to read a message from the client.
     *  (normally should be called if a SelectionKey for reading (event) is received (== client wrote something))
     * 
     * @param selectionKey Key from the selector that contains the data about the connection
     * @return Output that was sent by the client, that can therfore be used outside of the function!
     */
    public String readFromClient(SelectionKey selectionKey) {
        //System.out.println("> ServerHandler.readFromClient()");
        String output = "";

        try {
            SocketChannel sc = (SocketChannel) selectionKey.channel();
            this.byteBuffer.clear(); // Prepares the buffer for a new message
            int messageLength;
            try {
                messageLength = sc.read(this.byteBuffer);  // Reads the content that was received
            } catch (IOException e) {
                System.out.println("//" + Thread.currentThread().getName() + "// [IOException] Problem while trying to read through the SocketChannel, closing connection with it.");
                selectionKey.cancel();
                sc.close();
                return output;
            }
            if (messageLength == -1) {
                System.out.println("//" + Thread.currentThread().getName() + "// Nothing was read from client (SocketChannel.read()), closing connection with it.");
                sc.close();
                selectionKey.cancel();
                return output;
            }
            this.byteBuffer.flip();
            byte[] buffer = new byte[ServerHandler.DEFAULT_BYTEBUFFER_MESSAGE_LENGTH];
            this.byteBuffer.get(buffer, 0, messageLength); // Stores the message as bytes (because transfered as bytes in sockets)

            output = new String(buffer); // Gets the output as a String message
            
            ClientAttachment clientAttachment = (ClientAttachment) selectionKey.attachment();


            System.out.println("[MESSAGE FROM CLIENT] {"+clientAttachment.getConnectionId()+"}");
            System.out.println("\"" + output + "\"");

            ChatBot.prompt = output;
            
            selectionKey.interestOps(SelectionKey.OP_WRITE); // Prepares to write to the client in response to its request

            sc.register(
                this.selector,
                SelectionKey.OP_WRITE,
                clientAttachment
            );
            System.out.println(" >> Ready to WRITE to client[" + clientAttachment.getConnectionId() + "] << ");


        } catch (IOException e) {
            System.out.println("[IOException] Could not read from the client!");
            selectionKey.cancel();
            //return output;
        }

        return output;
    }

    /**
     * By default asks the ChatBot to input some message if none was passed as a parameter.
     * 
     * @param selectionKey Key from the selector that contains the data about the connection
     */
    public void writeToClient(SelectionKey selectionKey) {
        String text = "[default message from the server]";
        // In case we wanted to give the id of the connection to the client app...
        /* try {
            ClientAttachment clientAttachment = (ClientAttachment) selectionKey.attachment();
            text = clientAttachment.getConnectionId().toString();
        } catch (Exception e) {} */
        if (ChatBot.prompt != null) {
            text = ChatBot.getResponse(ChatBot.prompt);
        }
        this.writeToClient(selectionKey, text);
    }
    /**
     * Attempts to send a String message to a client.
     * 
     * @param selectionKey Key from the selector that contains the data about the connection
     * @param text Message to send to the server
     */
    public void writeToClient(SelectionKey selectionKey, String text) {
        //System.out.println("> ServerHandler.writeToClient()");

        try {
            SocketChannel sc = (SocketChannel) selectionKey.channel();
            ClientAttachment clientAttachment = (ClientAttachment) selectionKey.attachment();

            System.out.println("WRITING TARGET: client[" + clientAttachment.getConnectionId() + "]");
            System.out.println("MESSAGE: \"" + text + "\"");
            byte[] byteData = text.getBytes();
            sc.write(
                ByteBuffer.wrap(byteData) // Transfers the message as bytes in a ByteBuffer through the socket
            );
            System.err.println("Message sent.");


            selectionKey.interestOps(SelectionKey.OP_READ);  // Prepares a future request from that client

            sc.register(
                this.selector,
                SelectionKey.OP_READ,
                clientAttachment
            );

            System.out.println(" >> Ready to READ from client[" + clientAttachment.getConnectionId() + "] << ");
            
        } catch (IOException e) {
            System.out.println("[IOException] Could not write to the client!");
            selectionKey.cancel();
        }
    }

    /**
     * Attempts to close the connection with the server.
     */
    public void closeServerConnection() {
        System.out.println("Closing server connections...");
        if (this.selector != null) {
            try {
                this.selector.close();
                this.serverSocketChannel.socket().close();
                this.serverSocketChannel.close();
                this.threadPool.shutdownNow();
            } catch (IOException e) {
                System.out.println("[IOException] Server connection could not be closed.");
            }
        }
    }


}
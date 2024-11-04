
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;



/**
 * Class (and file) to be used to launch the server application.
 */
public class Main{
    
    /**
     * ServerHandler object (singleton?) that should be initialized later.
     */
    private static ServerHandler serverHandler;

    /**
     * Entry point of the program (function that is called).
     *  (https://stackoverflow.com/questions/890966/what-is-the-string-args-parameter-in-the-main-method)
     * 
     * @param args List of arguments that can be passed when starting the program, but not used here
     */
    public static void main(String[] args) {
        System.out.println("> main()");

        try {  
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();  
            while (networkInterfaces.hasMoreElements()) {  
                NetworkInterface networkInterface = networkInterfaces.nextElement();  
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();  
                while (addresses.hasMoreElements()) {  
                    InetAddress address = addresses.nextElement();  
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {  
                        //System.out.println("Local IP Address: " + address.getHostAddress());
                        ServerHandler.SERVER_HOST = address.getHostAddress();
                    }  
                }  
            }  
        } catch (SocketException ex) {  
            System.err.println("["+ex.getClass()+"] " + ex.getMessage());
        }

        if(args[0] != null) {
            ServerHandler.SERVER_PORT = Integer.valueOf(args[0]);
        }


        Main.serverHandler = ServerHandler.getInstance(); // Creates the ServerHandler, which by default initiates the communication with the server on another thread
        

        // If we coded something here, the connection should be almost set
        // But not exactly sure if we could immediately place "Main.serverHandler.writeToServer(blablabla)"
        // Especially since we don't have the SelectionKey
        // But there has to be a way
    }

}
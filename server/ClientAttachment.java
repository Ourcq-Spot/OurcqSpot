/**
 * Used to keep track of things related to a connection, by attaching this kind of object to SelectionKeys.
 *  (For instance, the ud of the user that is connected, or else...)
 */
public class ClientAttachment{

    /**
     * ID to give to the next user to connect.
     */
    private static Integer _connectionNextId = 0;
    /**
     * ID of the connection (could be id of the user).
     */
    private final Integer connectionId;
    /**
     * Flag that indicates whether a Thread is already running for some read/write event for this connection.
     */
    private volatile boolean isProcessing = false;
    

    /**
     * Constructor of the ClientAttachment.
     *  (initiates its connectionId property to a still unused Id)
     */
    public ClientAttachment(){
        this.connectionId = ClientAttachment.getAndIncConnectionNextId();
    }


    /**
     * Retrieves the connectionId of the client.
     * 
     * @return Connection ID of the client/connection
     */
    public Integer getConnectionId() {
        return this.connectionId;
    }
    /**
     * Retrieves the connectionId of the client and increments just after that.
     * 
     * @return Connection ID of the client/connection before its incrementation
     */
    public static Integer getAndIncConnectionNextId(){
        return ClientAttachment._connectionNextId++;
    }
    /**
     * Checks if the client is already processing something (read/write).
     *  > Do not respond to a write/read event if some action is still not finished!
     * 
     * @return The isProcessing property
     */
    public boolean isProcessing(){
        return this.isProcessing;
    }


    /**
     * By default, registers the connection as processing something.
     * 
     * @return Self (ClientAttachment instance) (I think not used but I don't care)
     */
    public ClientAttachment setProcessing(){
        return this.setProcessing(true);
    }
    /**
     * Registers the connection as processing something or not.
     * 
     * @param isProcessing The value we want the "isProcessing" property to be set to
     * @return Self (ClientAttachment instance) (I think not used but I don't care)
     */
    public ClientAttachment setProcessing(boolean isProcessing){
        this.isProcessing = isProcessing;
        return this;
    }
    /**
     * Registers the connection as not processing anything.
     * 
     * @return Self (ClientAttachment instance) (I think not used but I don't care)
     */
    public ClientAttachment unsetProcessing(){
        return this.setProcessing(false);
    }

}
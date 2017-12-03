package se.kth.id1212.filemanage.server.model;

import java.rmi.RemoteException;
import se.kth.id1212.filemanage.common.MessageException;
import se.kth.id1212.filemanage.common.FileAccessClient;

public class User {

    private final FileAccessClient remoteNode;
    private String username;
    private String lastTransferFilename; 

    public User(String username, FileAccessClient remoteNode) {
        this.username = username;
        this.remoteNode = remoteNode;
        
    }

    public void send(String msg) {
        try {
            remoteNode.recvMsg(msg);
        } catch (RemoteException re) {
            throw new MessageException("Failed to deliver message to " + username + ".");
        }
    }

    public void setLastTransferFilename(String filename) {
        this.lastTransferFilename = filename;
    }
        
    public String getLastTransferFilename() {
        return this.lastTransferFilename;
    }
    
    

}

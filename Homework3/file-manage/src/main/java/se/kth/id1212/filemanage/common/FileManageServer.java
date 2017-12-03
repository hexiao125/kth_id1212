package se.kth.id1212.filemanage.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileManageServer extends Remote {
   
    public static final String SERVER_NAME_IN_REGISTRY = "FILE_MANAGE_SERVER";

    boolean register(Credentials credentials) throws RemoteException;
    
    boolean unregister(Credentials credentials) throws RemoteException;
    
    boolean login(FileAccessClient remoteNode, Credentials credentials) throws RemoteException;
    
    boolean logout(String username) throws RemoteException;

    boolean addFile(FileInfo file) throws RemoteException;
    
    boolean getFile(String username, String fileName) throws RemoteException;
    
    boolean deleteFile(String userName, String fileName) throws RemoteException;
    
    String listFiles(String username) throws RemoteException;
    
    boolean updateFile(String username, FileInfo file) throws RemoteException;
   
}

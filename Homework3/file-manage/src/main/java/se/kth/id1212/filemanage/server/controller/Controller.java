package se.kth.id1212.filemanage.server.controller;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.kth.id1212.filemanage.common.Credentials;
import se.kth.id1212.filemanage.common.FileInfo;
import se.kth.id1212.filemanage.server.model.FileCatalogManager;
import se.kth.id1212.filemanage.common.FileManageServer;
import se.kth.id1212.filemanage.common.FileAccessClient;


public class Controller extends UnicastRemoteObject implements FileManageServer {
    private final FileCatalogManager fileManager = new FileCatalogManager();
  
    
    public Controller() throws RemoteException {
    }
      
    @Override
    public boolean register(Credentials credentials) {
        return fileManager.register(credentials);
    }
    
    @Override
    public boolean unregister(Credentials credentials) {
        return fileManager.unregister(credentials);
    }    
    
    @Override
    public boolean login(FileAccessClient remoteNode, Credentials credentials) {
        try {
            return fileManager.loginUser(remoteNode, credentials);
        } catch (SQLException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    @Override
    public boolean logout(String username) {
        return fileManager.logoutUser(username);
    }
    
    @Override
    public boolean addFile(FileInfo file) {
        return fileManager.addFile(file);
    }
    
    @Override
    public boolean deleteFile(String userName, String fileName) {
        return fileManager.deleteFile(userName,fileName);
    }
    
    @Override
    public boolean getFile(String username, String fileName) {
        try {
            return fileManager.getFile(username, fileName);
        } catch (SQLException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    @Override
    public String listFiles(String username) {
        try {
            return fileManager.listFiles(username);
        } catch (SQLException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
        
    @Override
    public boolean updateFile(String username, FileInfo file) {
        try {
            return fileManager.updateFile(username, file);
        } catch (SQLException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public String getLastFilename(String username){
        return fileManager.getLastFilename(username);
    }  
        
}

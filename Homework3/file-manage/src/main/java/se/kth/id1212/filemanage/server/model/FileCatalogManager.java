package se.kth.id1212.filemanage.server.model;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import se.kth.id1212.filemanage.common.Credentials;
import se.kth.id1212.filemanage.common.FileInfo;
import se.kth.id1212.filemanage.common.FileAccessClient;
import se.kth.id1212.filemanage.server.integration.JDBCHandler;

public class FileCatalogManager {
    private final Map<String, User> loginUsers = Collections.synchronizedMap(new HashMap<>());

    // in use
    private final JDBCHandler dbHandler = new JDBCHandler();
            
    public FileCatalogManager(){
        this.dbHandler.start();
    }        
      
    public boolean register(Credentials credentials){
        return dbHandler.addUser(credentials); 
    }
    
    public boolean unregister(Credentials credentials){
        return dbHandler.deleteUser(credentials); 
    }
        
   public boolean loginUser(FileAccessClient remoteNode, Credentials credentials) throws SQLException {
        if (dbHandler.userAuthentication(credentials)){
            if(!loginUsers.containsKey(credentials.getUsername())){
                User newUser = new User(credentials.getUsername(), remoteNode);
                loginUsers.put(credentials.getUsername(), newUser);
                return true;
            }
        }
        return false;
    }

    public boolean logoutUser(String username) {
        if (loginUsers.containsKey(username)) {
            loginUsers.remove(username);
            return true;
        }
        return false;
    }
    
    public boolean addFile(FileInfo file){
        if (loginUsers.containsKey(file.getOwner())){
            if (dbHandler.addFile(file)){
                loginUsers.get(file.getOwner()).setLastTransferFilename(file.getName());
                return true;
            }
            return false; 
        } else {
            return false;
        }
    }    

    public String getLastFilename(String username){
        return  loginUsers.get(username).getLastTransferFilename();
    }   
    
    public boolean getFile(String username, String filename) throws SQLException{
        if (loginUsers.containsKey(username)){
            if (dbHandler.getFile(username, filename)){
                if (dbHandler.getFileInform(filename)){
                    String owner = dbHandler.getFileOwner(filename);
                    notifyAccessToOwner(username, filename, owner, true);
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }   
    
    public boolean updateFile(String username, FileInfo file) throws SQLException{
        if (loginUsers.containsKey(username)) {  
            if (dbHandler.updateFile(username, file)) {
                String filename = file.getName();
                if (dbHandler.getFileInform(filename) && !dbHandler.getFileOwner(filename).equalsIgnoreCase(username)){
                    String owner = dbHandler.getFileOwner(filename);
                    notifyAccessToOwner(username, filename, owner, false);
                }
                return true;
            };
            return false;
        }
        return false;
    }
      
    private void notifyAccessToOwner(String username, String filename, String owner, boolean isRead){
        if (loginUsers.containsKey(owner)){
            if (isRead){
                loginUsers.get(owner).send("INFO from server: " + username + " has read your file " + filename + ".");
            } else {
                loginUsers.get(owner).send("INFO from server: " + username + " has updated your file " + filename + ".");
            }
 
        }
    }
    
    public boolean deleteFile(String userName,String fileName) {
        if (loginUsers.containsKey(userName)) {
            return dbHandler.deleteFile(userName, fileName);
        } else {
            return false;
        }
    }
    
    public String listFiles(String username) throws SQLException {
        return dbHandler.listFiles(username);
    }
     
    void broadcast(String msg) {
        synchronized (loginUsers) {
            for (User user : loginUsers.values()) {
                user.send(msg);
            }
        }
    }

}

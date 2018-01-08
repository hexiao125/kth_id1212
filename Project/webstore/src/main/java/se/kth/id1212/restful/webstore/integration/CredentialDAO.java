package se.kth.id1212.restful.webstore.integration;

import java.util.HashMap;
import java.util.Map;

public class CredentialDAO {
    private static final Map<String, Credential> credtMap = new HashMap<>(); ; 
    
    public CredentialDAO(){
    }
    
    public boolean checkCredt(Credential credt){
        String username = credt.getUsername();
        String password = credt.getPassword();
        if(userExist(username)){
            return credtMap.get(username).getPassword().equals(password);  
        }
        return false;       
    }    
    
    public boolean addUser(Credential credt){
        String username = credt.getUsername();
        String password = credt.getPassword();
        if(!userExist(username)){
            Credential newCredt= new Credential(username, password);
            credtMap.put(username,newCredt);
            return true;
        }
        return false;       
    }
    
    public boolean userExist(String username){
        return credtMap.containsKey(username);
    }
    
}

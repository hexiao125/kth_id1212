package se.kth.id1212.restful.webstore.model;

import se.kth.id1212.restful.webstore.integration.Credential;
import se.kth.id1212.restful.webstore.integration.CredentialDAO;

public class CredentialHandler {

    private final CredentialDAO credtDB = new CredentialDAO();
     
    public CredentialHandler() {
    }
    
    public Credential createUser(Credential credt){
        Credential regStatus = credt;
        if (credt.getUsername().isEmpty() || credt.getPassword().isEmpty()) {
            regStatus.setCredtPass(false);
        } else {
            boolean credtPass = credtDB.addUser(credt);
            regStatus.setCredtPass(credtPass);
            regStatus.setPassword(null);
        }
        return regStatus;
    }
        
    public Credential checkCredt(Credential credt){
        Credential logStatus = credt;
        logStatus.setCredtPass(credtDB.checkCredt(credt));
        logStatus.setPassword(null);
        return logStatus;
    }
}

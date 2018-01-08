package se.kth.id1212.restful.webstore.integration;

public class Credential {
    
    private String username;
    private String password;
    private boolean credtPass;
    
    public Credential(){
    }
    
    public Credential(String username, String password){
        this.username = username;
        this.password = password;
        this.credtPass = false;
    }
    
    public String getUsername(){
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean getCredtPass() {
        return credtPass;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCredtPass(boolean credtPass) {
        this.credtPass = credtPass;
    }
   
}

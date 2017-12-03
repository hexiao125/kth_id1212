package se.kth.id1212.filemanage.common;

import java.io.Serializable;

public class Credentials implements Serializable {
    private final String username;
    private final String password;

    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}

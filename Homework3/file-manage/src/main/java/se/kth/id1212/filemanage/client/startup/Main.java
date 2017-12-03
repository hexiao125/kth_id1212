package se.kth.id1212.filemanage.client.startup;

import java.io.IOException;
import java.rmi.RemoteException;
import se.kth.id1212.filemanage.client.view.NonBlockingUI;

public class Main {
  
    public static void main(String[] args) throws IOException {
        try {
            new NonBlockingUI().start();
            System.out.println("Please connect to the server by: connect <Server ip address>");
        } catch (RemoteException ex) {
            System.out.println("Could not start client.");
        }
    }
}

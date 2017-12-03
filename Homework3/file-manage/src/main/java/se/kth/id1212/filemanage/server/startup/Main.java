package se.kth.id1212.filemanage.server.startup;

import java.net.MalformedURLException;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import se.kth.id1212.filemanage.server.controller.Controller;
import se.kth.id1212.filemanage.server.net.FileHandlerServer;


public class Main {
  
    public static void main(String[] args) throws IOException{
        try {
            new Main().startRegistry();
            Controller ctrl = new Controller();
            Naming.rebind(Controller.SERVER_NAME_IN_REGISTRY, ctrl);
            System.out.println("File catalog server is running.");
        } catch (MalformedURLException | RemoteException ex) {
            System.out.println("Could not start file catalog server.");
        }     
        
        // file transfer server 
        FileHandlerServer server = new FileHandlerServer();
        System.out.println("File transfer server is running.");
        server.serve();   
    }
    
    private void startRegistry() throws RemoteException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException noRegistryIsRunning) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
    }
}

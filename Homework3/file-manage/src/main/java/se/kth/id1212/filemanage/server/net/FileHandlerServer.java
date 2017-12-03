package se.kth.id1212.filemanage.server.net;

import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class FileHandlerServer {
    private final List<FileHandler> connectedUser = new ArrayList<>();
    private final int portNo = 8888;
    private static final int LINGER_TIME = 3000;
    private static final int TIMEOUT_HALF_HOUR = 1800000;

    void removeHandler(FileHandler handler) {
        synchronized (connectedUser) {
            connectedUser.remove(handler);
        }
    }

    public void serve() {
        try {
            ServerSocket serverSocket = new ServerSocket(portNo);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                startHandler(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Server failure.");
        }
    }

    private void startHandler(Socket clientSocket) throws SocketException {
        clientSocket.setSoLinger(true, LINGER_TIME);
        clientSocket.setSoTimeout(TIMEOUT_HALF_HOUR);
        FileHandler handler = new FileHandler(this, clientSocket);
        synchronized (connectedUser) {
            connectedUser.add(handler);
        }
        Thread handlerThread = new Thread(handler);
        handlerThread.setPriority(Thread.MAX_PRIORITY);
        handlerThread.start();
    }
        
}

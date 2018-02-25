package se.kth.id1212.sockets.hangman.server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import se.kth.id1212.sockets.hangman.common.Message;
import se.kth.id1212.sockets.hangman.server.controller.Controller;

public class GameServer {
    private static final int LINGER_TIME = 5000;
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private final Controller contr = new Controller();
    private final HashMap<Integer, GameSession> clients = new HashMap<>();
    private int portNo = 8080;
    private int genId = 0;


    public static void main(String[] args) {
        GameServer server = new GameServer();
        server.parseArguments(args);
        server.serve();
    }

    private void serve() {
        try {
            ServerSocket listeningSocket = new ServerSocket(portNo);
            System.out.println("Game server is running.");
            while (true) {
                Socket clientSocket = listeningSocket.accept();
                startSession(clientSocket);
            }
        } catch (IOException ioe) {
            System.err.println("Server failure.");
        }
    }

    private void startSession(Socket clientSocket) throws SocketException {
        int clientId =  newClientId();
        clientSocket.setSoLinger(true, LINGER_TIME);
        clientSocket.setSoTimeout(TIMEOUT_HALF_HOUR);
        GameSession game = new GameSession(this,clientSocket,clientId);
        this.clients.put(clientId, game);
        Thread gameThread = new Thread(game);
        gameThread.setPriority(Thread.MAX_PRIORITY);
        gameThread.start();
    }

    void removeSession(int clientId) {
        this.clients.remove(clientId);
    }
        
    private int newClientId(){
        this.genId ++;
        return this.genId;
    }
 
    public Message startGame(int clientId){
        return this.contr.startGame(clientId);
    }

    public Message checkPlayerGuess(String input, int clientId){
        return this.contr.checkPlayerGuess(input, clientId);
    }
    
    public int getScore(int clientId){
        return this.contr.getScore(clientId);
    }
    
    private void parseArguments(String[] args){
        if (args.length > 0) {
            try {
                portNo = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number, using default.");
            }
        }
    }
}

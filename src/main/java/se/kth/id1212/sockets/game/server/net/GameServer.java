/*
 * The MIT License
 *
 * Copyright 2017 Leif Lindbäck <leifl@kth.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package se.kth.id1212.sockets.game.server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import se.kth.id1212.sockets.game.server.controller.Controller;
import se.kth.id1212.sockets.game.server.model.Observer;

/**
 * Receives chat messages and broadcasts them to all chat clients. All communication to/from any
 * chat node pass this server.
 */
public class GameServer implements Observer {
    private static final int LINGER_TIME = 5000;
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private final Controller contr = new Controller();
    private final List<ClientHandler> clients = new ArrayList<>();
    private int portNo = 8080;
    private String ownClientName = new String();
    private String msgToClient = new String();
    
    /**
     * @param args Takes one command line argument, the number of the port on which the server will
     *             listen, the default is <code>8080</code>.
     */
    public static void main(String[] args) {
        GameServer server = new GameServer();
        server.getController().addObserver(server);
        server.parseArguments(args);
        server.serve();
        
    }

    /**
     * Sends the specified message to all connected clients
     *
     * @param msg The message to broadcast.
     */
    void broadcast(String msg) {
        contr.appendEntry(msg);
        synchronized (clients) {
            clients.forEach((client) -> client.sendMsg(msg));
        }
    }
        
    /**
     * Sends the dedicated score to all connected clients
     *
     */
    void unicastScore(boolean updateLastRound) {
        synchronized (clients) {
            for (ClientHandler client : clients){
                String username = client.getUserName();
                int lastScore = contr.getLastScore(username);
                int totalScore = contr.getTotalScore(username);
                String gameResult = contr.getGameResult(username);
                if (!updateLastRound){
                    // calculate last round score
                    client.sendMsg(gameResult + "  Last Round Score: " + lastScore
                            + "  Total Score: " + totalScore
                            + "  <play> one more game or <quit>?");
                } else {
                    // clear last round score
                    client.sendMsg("New round has started!  Last Round Score: " + lastScore
                            + "  Total Score: " + totalScore);
                }
            }
        }
    }

    /**
     * The chat client handled by the specified <code>ClientHandler</code> has disconnected from the
     * server, and shall not participate in any future communication.
     *
     * @param handler The handler of the disconnected client.
     */
    void removeHandler(ClientHandler handler) {
        synchronized (clients) {
            clients.remove(handler);
        }
    }

    public void serve() {
        try {
            ServerSocket listeningSocket = new ServerSocket(portNo);
            while (true) {
                Socket clientSocket = listeningSocket.accept();
                startHandler(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Server failure.");
        }
    }

    private void startHandler(Socket clientSocket) throws SocketException {
        clientSocket.setSoLinger(true, LINGER_TIME);
        clientSocket.setSoTimeout(TIMEOUT_HALF_HOUR);
        ClientHandler handler = new ClientHandler(this, clientSocket, contr.getGameModel());
        synchronized (clients) {
            clients.add(handler);
        }
        Thread handlerThread = new Thread(handler);
        handlerThread.setPriority(Thread.MAX_PRIORITY);
        handlerThread.start();
    }

    public void parseArguments(String[] arguments) {
        if (arguments.length > 0) {
            try {
                portNo = Integer.parseInt(arguments[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number, using default.");
            }
        }
    }
    
    public Controller getController(){
        return this.contr;
    }
    
    public void setOwnClientName(String msg){
        this.ownClientName = msg;
    }
     
    /*
    ** update Observer
    */
    @Override
    public void update(){
        if (this.contr.checkUpdateUserInfo()) {
            msgToClient = this.contr.getMsgToClient();
            broadcast(msgToClient);
        }
        if (this.contr.checkUpdateScore()){
            unicastScore(false);
        }
        if (this.contr.checkUpdateNewRound()){
            unicastScore(true);
        }
    }
       
    public String getMsgToClient() {
        return msgToClient;
    }
    
    public boolean checkOwnClientSet(){
        return this.contr.checkOwnClientSet();
    }
    
    public void setOwnClient(String userName){
        this.contr.setOwnClient(userName);
    }
    
    public String getOwnClientName(){
        return this.contr.getOwnClientName();
    }   
    
}

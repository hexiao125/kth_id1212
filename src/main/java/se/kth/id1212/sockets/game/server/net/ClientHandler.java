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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.StringJoiner;
import se.kth.id1212.sockets.game.common.Constants;
import se.kth.id1212.sockets.game.common.MsgType;
import se.kth.id1212.sockets.game.common.MessageException;
import se.kth.id1212.sockets.game.server.controller.Controller;
import se.kth.id1212.sockets.game.server.model.Message;

/**
 * Handles all communication with one particular chat client.
 */
public class ClientHandler implements Runnable{
    private final GameServer server;
    private final Socket clientSocket;
    private final String[] communicationWhenStarting;
    private BufferedReader fromClient;
    private PrintWriter toClient;
    private String username = "anonymous";
    private boolean connected;
    private Controller contr; 
    

    /**
     * Creates a new instance, which will handle communication with one specific client connected to
     * the specified socket.
     *
     * @param clientSocket The socket to which this handler's client is connected.
     */
    ClientHandler(GameServer server, Socket clientSocket, String[] communication) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.communicationWhenStarting = communication;
        connected = true;
        this.contr = server.getController();
    }

    /**
     * The run loop handling all communication with the connected client.
     */
    @Override
    public void run() {
        try {
            boolean autoFlush = true;
            fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            toClient = new PrintWriter(clientSocket.getOutputStream(), autoFlush);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
        for (String entry : communicationWhenStarting) {
            sendMsg(entry);
        }
        while (connected) {
            try {
                Message msg = new Message(fromClient.readLine());
                updateClientAction(msg);
            } catch (IOException ioe) {
                disconnectClient();
                throw new MessageException(ioe);
            }
        }
    }

    public void updateClientAction(Message msg) {
        switch (msg.getMsgType()) {
            case USER:
                if (!username.equals("anonymous")) {
                    // not allowed to change username
                    sendMsg("Sorry! You are not allowed to change your username!");
                } else if (contr.checkUserExist(msg.getMsgBody())) {
                    // Inform client to create another name
                    sendMsg("This username is in use now, please specify a new name!");
                } else {
                    username = msg.getMsgBody();
                    msg.setUserName(username);
                    contr.updateClientAction(msg);
                    sendMsg("Username has passed. You can start game by command <play>"
                            + " or quit game by command <quit>");
                }
                break;
            case ACTION:
                // sanity check
                if (msg.getMsgBody().toUpperCase().equals("PLAY")) {
                    if (contr.getReadyToPlay(username)) {
                        sendMsg("You have already started the game. Make a choice instead!");
                    } else {
                        msg.setUserName(username);
                        contr.updateClientAction(msg);
                        sendMsg("Time to make a choice from [paper,rock,scissor]!");
                    }
                } else if (msg.getMsgBody().toUpperCase().equals("PAPER")
                        || msg.getMsgBody().toUpperCase().equals("ROCK")
                        || msg.getMsgBody().toUpperCase().equals("SCISSOR")) {
                    if (contr.getReadyToPlay(username)) {
                        msg.setUserName(username);
                        sendMsg("Waiting for other players!");
                        contr.updateClientAction(msg);
                    } else {
                        sendMsg("Invalid perform! You must start the game first by command <play>!");
                    }
                } else // inform client on invalid input
                if (contr.getReadyToPlay(username)) {
                    sendMsg("Invalid perform! You must choose from [paper, rock, scissor]!");
                } else {
                    sendMsg("Invalid perform! You must start the game first by command <play>!");
                }
                break;
            default:
                if (username.equals("anonymous")) {
                    // Inform client to create another name
                    sendMsg("Please specify a username before starting game!");
                } else {
                    msg.setUserName(username);
                    contr.updateClientAction(msg);
                }
        }
    }
    
    /**
     * Sends the specified message to the connected client.
     *
     * @param msg The message to send.
     */
    void sendMsg(String msg) {
        if (this.server.getOwnClientName().equals(username)) {
            StringJoiner joiner = new StringJoiner(Constants.MSG_DELIMETER);
            joiner.add(MsgType.UNICAST.toString());
            joiner.add(msg);
            toClient.println(joiner.toString());
        }
    }

    private void disconnectClient() {
        try {
            clientSocket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        connected = false;
        server.removeHandler(this);
    }
    
    public String getUserName(){
        return this.username;
    }
}

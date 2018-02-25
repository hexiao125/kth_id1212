package se.kth.id1212.sockets.hangman.server.net;

import se.kth.id1212.sockets.hangman.common.Message;
import se.kth.id1212.sockets.hangman.common.MessageException;
import se.kth.id1212.sockets.hangman.common.MsgType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;


public class GameSession implements Runnable {
    private final Socket clientSocket;
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;
    private static final String WELCOME_MESSAGE = "Welcome to Hangman!";
    private static final String YOUR_SCORE = "Your current score is: ";
    private static final String INSTRUCTIONS = "Enter <start> to start a new game, or <quit> to quit the game.";
    private final GameServer server;
    private boolean connected;
    private boolean gameStarted = false;
    private final int clientId;

    GameSession(GameServer server, Socket clientSocket, int clientId) {
        this.server = server;
        this.clientSocket = clientSocket;
        connected = true;
        this.clientId = clientId;
    }

    @Override
    public void run() {
        try {
            fromClient = new ObjectInputStream(clientSocket.getInputStream());
            toClient = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch(IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
        sendMsg(MsgType.WELCOME, WELCOME_MESSAGE);
        while (connected) {
            try {
                if(!(gameStarted)) {
                    sendMsg(MsgType.WELCOME, INSTRUCTIONS);
                }
                Message msg = (Message) fromClient.readObject();
                switch(msg.getType()) {
                    case START:
                        if (!(gameStarted)) {
                            gameStarted = true;
                            Message hint = server.startGame(this.clientId);
                            sendMsg(hint);
                        }
                        break;
                    case QUIT:
                        disconnectClient();
                        break;
                    case INPUT:
                        if (gameStarted) {
                            Message hint = server.checkPlayerGuess(msg.getBody(),this.clientId);
                            sendMsg(hint);
                            if (hint.getType() == MsgType.GAMEOVER) {
                                gameStarted = false;
                                sendMsg(MsgType.WELCOME, YOUR_SCORE + server.getScore(this.clientId));
                            }
                        }
                        break;
                   default:
                        break;
                }
            } catch (IOException | ClassNotFoundException e) {
                disconnectClient();
                throw new MessageException(e);
            }
        }
    }

    private void disconnectClient() {
        try {
            clientSocket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        connected = false;
        server.removeSession(this.clientId);
    }

    private void sendMsg(MsgType msgType, String msgBody) throws UncheckedIOException {
        try {
            Message msg = new Message(msgType, msgBody);
            toClient.writeObject(msg);
            toClient.flush();
            toClient.reset();
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private void sendMsg(Message msg) throws UncheckedIOException {
        try {
            toClient.writeObject(msg);
            toClient.flush();
            toClient.reset();
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }
}

package se.kth.id1212.nio.hangman.server.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.StringJoiner;
import se.kth.id1212.nio.hangman.common.Message;
import se.kth.id1212.nio.hangman.common.MessageException;
import se.kth.id1212.nio.hangman.common.MessageSplitter;
import se.kth.id1212.nio.hangman.common.MsgType;
import se.kth.id1212.nio.hangman.server.controller.Controller;


public class GameServer {
    private static final int LINGER_TIME = 5000;
    private final Controller contr = new Controller();
    private final Queue<ByteBuffer> resultToSend = new ArrayDeque<>();
    private int portNo = 8080;
    private Selector selector;
    private ServerSocketChannel listeningSocketChannel;
    private volatile boolean timeToSend = false;

    public Controller getController(){
        return this.contr;
    }
    
    public void sendResultToClient(String receiver, String msg) {
        timeToSend = true;
        StringJoiner joiner = new StringJoiner("##");
        joiner.add(MsgType.RESULT.toString());
        joiner.add(receiver);
        joiner.add(msg);
        ByteBuffer completeMsg = ByteBuffer.wrap(MessageSplitter.prependLengthHeader(joiner.toString()).getBytes());
        synchronized (resultToSend) {
            resultToSend.add(completeMsg);
        }
        selector.wakeup();
    }

    private void serve() {
        try {
            initServer();
            System.out.println("Game server is running.");
            while (true) {
                if (timeToSend) {
                    writeForAllClients();
                    appendMsgToAllClientQueues();
                    timeToSend = false;
                }
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        startSession(key);
                    } else if (key.isReadable()) {
                        recvFromClient(key);
                    } else if (key.isWritable()) {
                        sendToClient(key);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Server failure.");
        }
    }

    private void startSession(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        GameSession gameSession = new GameSession(this, clientChannel);
        clientChannel.register(selector, SelectionKey.OP_WRITE, gameSession);
        clientChannel.setOption(StandardSocketOptions.SO_LINGER, LINGER_TIME); 
        
    }

    private void recvFromClient(SelectionKey key) throws IOException {
        GameSession gameSession = (GameSession) key.attachment();
        try {
            gameSession.recvMsg();
        } catch (IOException playerHasClosedConnection) {
            removeClient(key);
        }
    }

    private void sendToClient(SelectionKey key) throws IOException {
        GameSession gameSession = (GameSession) key.attachment();
        try {
            gameSession.sendAll();
            key.interestOps(SelectionKey.OP_READ);
        } catch (MessageException couldNotSendAllMessages) {
        } catch (IOException clientHasClosedConnection) {
            removeClient(key);
        }
    }

    private void removeClient(SelectionKey key) throws IOException {
        GameSession gameSession = (GameSession) key.attachment();
        gameSession.disconnectClient();
        key.cancel();
    }

    private void initServer() throws IOException {
        selector = Selector.open();
        listeningSocketChannel = ServerSocketChannel.open();
        listeningSocketChannel.configureBlocking(false);
        listeningSocketChannel.bind(new InetSocketAddress(portNo));
        listeningSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void writeForAllClients() {
        for (SelectionKey key : selector.keys()) {
            if (key.channel() instanceof SocketChannel && key.isValid()) {
                key.interestOps(SelectionKey.OP_WRITE);
            }
        }
    }

    private void appendMsgToAllClientQueues() {
        synchronized (resultToSend) {
            ByteBuffer msgToSend;
            while ((msgToSend = resultToSend.poll()) != null) {
                for (SelectionKey key : selector.keys()) {
                    GameSession gameSession = (GameSession) key.attachment();
                    if (gameSession == null) {
                        continue;
                    }
                    synchronized (gameSession.getMessagesToSend()) {
                        gameSession.queueMsgToSend(msgToSend);

                    }
                }
            }
        }
    }
    
    public void createClient(String clientId){
        contr.createClient(clientId);
    }
    
    public void startGame(String clientId){
        Message rslt = contr.startGame(clientId);
        sendResultToClient(rslt.getUser(),rslt.getBody());
    }
    
    public boolean checkPlayerGuess(Message msg){
        Message rslt = contr.checkPlayerGuess(msg);
        sendResultToClient(rslt.getUser(),rslt.getBody());
        if (rslt.getType() == MsgType.GAMEOVER) {
            return true;
        }
        return false;
    }
   
    private void parseArguments(String[] arguments) {
        if (arguments.length > 0) {
            try {
                portNo = Integer.parseInt(arguments[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number, using default.");
            }
        }
    }
    
    public static void main(String[] args) {
        GameServer server = new GameServer();
        server.parseArguments(args);
        server.serve();
    }
        
}

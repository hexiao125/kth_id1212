package se.kth.id1212.nio.hangman.server.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ForkJoinPool;
import se.kth.id1212.nio.hangman.common.Message;
import se.kth.id1212.nio.hangman.common.MessageException;
import se.kth.id1212.nio.hangman.common.MessageSplitter;

class GameSession implements Runnable {

    private final GameServer server;
    private final SocketChannel clientChannel;
    private final ByteBuffer msgFromClient = ByteBuffer.allocateDirect(8192);
    private final Queue<ByteBuffer> messagesToSend = new ArrayDeque<>();
    private final MessageSplitter msgSplitter = new MessageSplitter();
    private boolean gameStarted = false;
    
    GameSession(GameServer server, SocketChannel clientChannel) {
        this.server = server;
        this.clientChannel = clientChannel;
    }

    public void queueMsgToSend(ByteBuffer msg) {
        synchronized (messagesToSend) {
            messagesToSend.add(msg.duplicate());
        }
    }

    public void sendAll() throws IOException, MessageException {
        ByteBuffer msg = null;
        synchronized (messagesToSend) {
            while ((msg = messagesToSend.peek()) != null) {
                sendMsg(msg);
                messagesToSend.remove();
            }
        }
    }
    
    public Queue<ByteBuffer> getMessagesToSend(){
        return this.messagesToSend;
    }
        
    @Override
    public void run() {
        while (msgSplitter.hasNext()) {
            Message msg = new Message(msgSplitter.nextMsg());
            switch (msg.getType()) {
                case USER:
                    server.createClient(msg.getUser());
                    break;
                case START:
                    if (!(gameStarted)) {
                        gameStarted = true;
                        server.startGame(msg.getUser());
                    }       
                    break;
                case INPUT:
                    gameStarted = (!server.checkPlayerGuess(msg));
                    break;
                case DISCONNECT:
                    break;
                default:
                    throw new MessageException("Received corrupt message: " + msg.getReceivedString());
            }
        }
    }

    void sendMsg(ByteBuffer msg) throws IOException {
        clientChannel.write(msg);
        if (msg.hasRemaining()) {
            throw new MessageException("Could not send message");
        }
    }

    void recvMsg() throws IOException {
        msgFromClient.clear();
        int numOfReadBytes;
        numOfReadBytes = clientChannel.read(msgFromClient);
        if (numOfReadBytes == -1) {
            throw new IOException("Client has closed connection.");
        }
        String recvdString = extractMessageFromBuffer();
        msgSplitter.appendRecvdString(recvdString);
        ForkJoinPool.commonPool().execute(this);
    }

    private String extractMessageFromBuffer() {
        msgFromClient.flip();
        byte[] bytes = new byte[msgFromClient.remaining()];
        msgFromClient.get(bytes);
        return new String(bytes);
    }

    void disconnectClient() throws IOException { 
        clientChannel.close();
    }

}

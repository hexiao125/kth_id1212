package se.kth.id1212.nio.hangman.client.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.StringJoiner;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import se.kth.id1212.nio.hangman.common.MessageSplitter;
import se.kth.id1212.nio.hangman.common.MsgType;

public class ServerConnection implements Runnable {
    private static final String FATAL_COMMUNICATION_MSG = "Lost connection.";
    private static final String FATAL_DISCONNECT_MSG = "Could not disconnect, force to leave.";

    private final ByteBuffer msgFromServer = ByteBuffer.allocateDirect(8192);
    private final Queue<ByteBuffer> messagesToSend = new ArrayDeque<>();
    private final MessageSplitter msgSplitter = new MessageSplitter();
    private final List<CommListener> listeners = new ArrayList<>();
    private InetSocketAddress serverAddress;
    private SocketChannel socketChannel;
    private Selector selector;
    private boolean connected;
    private volatile boolean timeToSend = false;
    private String username = "anonymous";

    @Override
    public void run() {
        try {
            initClient();

            while (connected || !messagesToSend.isEmpty()) {
                if (timeToSend) {
                    socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    timeToSend = false;
                }

                selector.select();
                for (SelectionKey key : selector.selectedKeys()) {
                    selector.selectedKeys().remove(key);
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isConnectable()) {
                        completeConnection(key);
                    } else if (key.isReadable()) {
                        msgFromServer(key);
                    } else if (key.isWritable()) {
                        msgToServer(key);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(FATAL_COMMUNICATION_MSG);
        }
        try {
            doDisconnect();
        } catch (IOException ex) {
            System.err.println(FATAL_DISCONNECT_MSG);
        }
    }

    public void addCommunicationListener(CommListener listener) {
        listeners.add(listener);
    }

    public void connect(String host, int port) {
        serverAddress = new InetSocketAddress(host, port);
        new Thread(this).start();
    }

    private void initClient() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(serverAddress);
        connected = true;
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
    }

    public void sendUsername(String username) {
        this.username = username;
        sendMsg(MsgType.USER.toString(), username, null);
    }

    public String getUsername(){
        return this.username;    
    }
    
    public void sendInput(String msg) {
        sendMsg(MsgType.INPUT.toString(), this.username, msg);
    }

    public void sendStart() {
        sendMsg(MsgType.START.toString(), this.username, null);
    }
        
    private void sendMsg(String... parts) {
        StringJoiner joiner = new StringJoiner("##");
        for (String part : parts) {
            joiner.add(part);
        }
        String messageWithLengthHeader = MessageSplitter.prependLengthHeader(joiner.toString());
        synchronized (messagesToSend) {
            messagesToSend.add(ByteBuffer.wrap(messageWithLengthHeader.getBytes()));
        }
        timeToSend = true;
        selector.wakeup();
    }
    
    private void completeConnection(SelectionKey key) throws IOException {
        socketChannel.finishConnect();
        key.interestOps(SelectionKey.OP_READ);
        try {
            InetSocketAddress remoteAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
                notifyConnectionDone(remoteAddress);
        } catch (IOException ioe) {
                notifyConnectionDone(serverAddress);
        }
    }

    public void disconnect() throws IOException {
        connected = false;
        sendMsg(MsgType.DISCONNECT.toString(), this.username, null);
    }

    private void doDisconnect() throws IOException {
        socketChannel.close();
        socketChannel.keyFor(selector).cancel();
        notifyDisconnectionDone();
    }

    private void msgToServer(SelectionKey key) throws IOException {
        ByteBuffer msg;
        synchronized (messagesToSend) {
            while ((msg = messagesToSend.peek()) != null) {
                socketChannel.write(msg);
                if (msg.hasRemaining()) {
                    return;
                }
                messagesToSend.remove();
            }
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    private void msgFromServer(SelectionKey key) throws IOException {
        msgFromServer.clear();
        int numOfReadBytes = socketChannel.read(msgFromServer);
        if (numOfReadBytes == -1) {
            throw new IOException(FATAL_COMMUNICATION_MSG);
        }
        String recvdString = extractMessage();
        msgSplitter.appendRecvdString(recvdString);
        while (msgSplitter.hasNext()) {
            String msg = msgSplitter.nextMsg();
            if (MessageSplitter.userOf(msg).equalsIgnoreCase(this.username)) {
                notifyMsgReceived(MessageSplitter.bodyOf(msg));
            }
        }
    }

    private String extractMessage() {
        msgFromServer.flip();
        byte[] bytes = new byte[msgFromServer.remaining()];
        msgFromServer.get(bytes);
        return new String(bytes);
    }
    
    private void notifyConnectionDone(InetSocketAddress connectedAddress) {
        Executor pool = ForkJoinPool.commonPool();
        for (CommListener listener : listeners) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    listener.connected(connectedAddress);
                }
            });
        }
    }
    
    private void notifyDisconnectionDone() {
        Executor pool = ForkJoinPool.commonPool();
        for (CommListener listener : listeners) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    listener.disconnected();
                }
            });
        }
    }
    
    private void notifyMsgReceived(String msg) {
        Executor pool = ForkJoinPool.commonPool();
        for (CommListener listener : listeners) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    listener.recvdMsg(msg);
                }
            });
        }
    }
}

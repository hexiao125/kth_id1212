package se.kth.id1212.sockets.hangman.client.net;

import se.kth.id1212.sockets.hangman.common.Message;
import se.kth.id1212.sockets.hangman.common.MsgType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerConnection
{
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private static final int TIMEOUT_HALF_MINUTE = 30000;
    private Socket socket;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;
    private boolean connected;

    public void connect(String host, int port, OutputHandler outputHandler) throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), TIMEOUT_HALF_MINUTE);
        socket.setSoTimeout(TIMEOUT_HALF_HOUR);
        connected = true;
        toServer = new ObjectOutputStream(socket.getOutputStream());
        fromServer = new ObjectInputStream(socket.getInputStream());
        new Thread(new Listener(outputHandler)).start();
    }

    private void sendMsg(Message msg) throws IOException {
        toServer.writeObject(msg);
        toServer.flush();
        toServer.reset();
    }

    public void disconnect() throws IOException {
        sendMsg(new Message(MsgType.QUIT, null));
        socket.close();
        socket = null;
        connected = false;
    }

    public void sendInput(Message msg) throws IOException {
        sendMsg(msg);
    }

    private class Listener implements Runnable {
        private final OutputHandler outputHandler;

        private Listener(OutputHandler outputHandler) { this.outputHandler = outputHandler; }

        @Override
        public void run() {
            try {
                for (;;) {
                    Message msg = (Message) fromServer.readObject();
                    String body = extractMsgBody(msg);
                    outputHandler.handleMsg(body);
                }
            } catch (Throwable connectionFailure) {
                if (connected) {
                    outputHandler.handleMsg("Lost connection.");
                }
            }
        }

        private String extractMsgBody(Message msg) {
            return msg.getBody();
        }

    }
}

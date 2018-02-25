package se.kth.id1212.nio.hangman.client.net;

import java.net.InetSocketAddress;

public interface CommListener {
 
    public void recvdMsg(String msg);

    public void connected(InetSocketAddress serverAddress);

    public void disconnected();
}

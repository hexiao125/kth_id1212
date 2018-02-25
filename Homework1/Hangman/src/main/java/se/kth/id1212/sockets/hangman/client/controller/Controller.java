package se.kth.id1212.sockets.hangman.client.controller;

import se.kth.id1212.sockets.hangman.client.net.OutputHandler;
import se.kth.id1212.sockets.hangman.client.net.ServerConnection;
import se.kth.id1212.sockets.hangman.common.Message;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;

public class Controller
{
    private final ServerConnection serverConnection = new ServerConnection();

    public void connect(String host, int port, OutputHandler outputHandler) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.connect(host, port, outputHandler);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        }).thenRun(() -> outputHandler.handleMsg("Connected to " + host + ":" + port));
    }

    public void disconnect() throws IOException {
        serverConnection.disconnect();
    }

    public void sendInput(Message msg) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.sendInput(msg);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        });
    }
}

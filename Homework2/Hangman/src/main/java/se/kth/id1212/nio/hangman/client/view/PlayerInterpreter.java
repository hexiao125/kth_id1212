package se.kth.id1212.nio.hangman.client.view;

import java.net.InetSocketAddress;
import java.util.Scanner;
import se.kth.id1212.nio.hangman.client.net.ServerConnection;
import se.kth.id1212.nio.hangman.client.net.CommListener;

public class PlayerInterpreter implements Runnable {
    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private boolean receivingCmds = false;
    private ServerConnection server;
    

    public void start() {
        if (receivingCmds) {
            return;
        }
        receivingCmds = true;
        server = new ServerConnection();
        new Thread(this).start();
    }

    @Override
    public void run() {
        System.out.println("> Please enter connect <server host> <port number> to connect game server.");
        while (receivingCmds) {
            try {
                CmdLine cmdLine = new CmdLine(readNextLine());
                switch (cmdLine.getCmd()) {
                    case QUIT:
                        receivingCmds = false;
                        server.disconnect();
                        break;
                    case CONNECT:
                        server.addCommunicationListener(new ConsoleOutput());
                        server.connect(cmdLine.getParameter(0),
                                       Integer.parseInt(cmdLine.getParameter(1)));
                        break;
                    case USER:
                        if (server.getUsername().equalsIgnoreCase("anonymous")){
                            server.sendUsername(cmdLine.getParameter(0));
                        }
                        System.out.println("> Enter <start> to start a new game, or <quit> to quit the game.");
                        break;
                    case START:
                        server.sendStart();
                        System.out.println("> Game started!");
                        break;
                    default:
                        server.sendInput(cmdLine.getUserInput());
                }
            } catch (Exception e) {
                System.out.println("Operation failed");
            }
        }
    }

    private String readNextLine() {
        System.out.print(PROMPT);
        return console.nextLine();
    }

    private class ConsoleOutput implements CommListener {
        @Override
        public void recvdMsg(String msg) {
            printToConsole(msg);
        }

        @Override
        public void connected(InetSocketAddress serverAddress) {
            printToConsole("Welcome to Hangman." 
            +"Please enter user <username> to specify a username for the game!");
        }

        @Override
        public void disconnected() {
            printToConsole("Disconnected from server.");
        }

        private void printToConsole(String output) {
            System.out.println(output);
            System.out.print(PROMPT);
        }
    }
}

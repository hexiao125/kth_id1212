package se.kth.id1212.sockets.hangman.client.view;

import se.kth.id1212.sockets.hangman.client.controller.Controller;
import se.kth.id1212.sockets.hangman.client.net.OutputHandler;
import se.kth.id1212.sockets.hangman.common.Message;
import se.kth.id1212.sockets.hangman.common.MsgType;

import java.util.Scanner;

public class PlayerInterpreter implements Runnable {
    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private boolean receivingCmds = false;
    private Controller contr;

    public void start() {
        if (receivingCmds) {
            return;
        }
        receivingCmds = true;
        contr = new Controller();
        new Thread(this).start();
    }

    @Override
    public void run() {
        System.out.println("Please enter connect <server host> <port number> to connect game server.");
        while (receivingCmds) {
            try {
                CmdLine cmdLine = new CmdLine(readNextLine());
                switch(cmdLine.getCmd()) {
                    case QUIT:
                        receivingCmds = false;
                        contr.disconnect();
                        break;
                    case CONNECT:
                        contr.connect(cmdLine.getParameter(0),
                                Integer.parseInt(cmdLine.getParameter(1)),
                                new ConsoleOutput());
                        break;
                    case START:
                        contr.sendInput(new Message(MsgType.START, null));
                        break;
                   default:
                        contr.sendInput(new Message(MsgType.INPUT, cmdLine.getParameter(0)));
                        break;
                }
            } catch (Exception e) {
                System.out.println("Operation failed");
            }
        }
    }

    private String readNextLine() {
        System.out.print(PROMPT);
        String nextLine = console.nextLine();
        while (nextLine.isEmpty()) {
            nextLine = console.nextLine();
        }
        return nextLine;
    }

    private class ConsoleOutput implements OutputHandler {
        @Override
        public void handleMsg(String msg) {
            System.out.println(msg);
            System.out.print(PROMPT);
        }
    }
}

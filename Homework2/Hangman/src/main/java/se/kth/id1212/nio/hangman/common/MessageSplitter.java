package se.kth.id1212.nio.hangman.common;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.StringJoiner;

public class MessageSplitter {
    private StringBuilder recvdChars = new StringBuilder();
    private final Queue<String> messages = new ArrayDeque<>();

    public synchronized void appendRecvdString(String recvdString) {
        recvdChars.append(recvdString);
        while(extractMsg());
    }

    public synchronized String nextMsg() {
        return messages.poll();
    }

    public synchronized boolean hasNext() {
        return !messages.isEmpty();
    }

    public static String prependLengthHeader(String msgWithoutHeader) {
        StringJoiner joiner = new StringJoiner("###");
        joiner.add(Integer.toString(msgWithoutHeader.length()));
        joiner.add(msgWithoutHeader);
        return joiner.toString();
    }

    public static MsgType typeOf(String msg) {
        String[] msgParts = msg.split("##");
        return MsgType.valueOf(msgParts[0].toUpperCase());
    }
    public static String userOf(String msg) {
        String[] msgParts = msg.split("##");
        return msgParts[1];
    }
   
    public static String bodyOf(String msg) {
        String[] msgParts = msg.split("##");
        return msgParts[2];
    }

    private boolean extractMsg() {
        String allRecvdChars = recvdChars.toString();
        String[] splitAtHeader = allRecvdChars.split("###");
        if (splitAtHeader.length < 2) {
            return false;
        }
        String lengthHeader = splitAtHeader[0];
        int lengthOfFirstMsg = Integer.parseInt(lengthHeader);
        if (hasCompleteMsg(lengthOfFirstMsg, splitAtHeader[1])) {
            String completeMsg = splitAtHeader[1].substring(0, lengthOfFirstMsg);
            messages.add(completeMsg);
            recvdChars.delete(0, lengthHeader.length()
                                 + 3 + lengthOfFirstMsg);
            return true;
        }
        return false;
    }

    private boolean hasCompleteMsg(int msgLen, String recvd) {
        return recvd.length() >= msgLen;
    }

}

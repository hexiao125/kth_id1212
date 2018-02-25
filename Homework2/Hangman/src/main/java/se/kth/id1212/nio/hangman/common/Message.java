package se.kth.id1212.nio.hangman.common;

import java.io.Serializable;

public class Message implements Serializable {
    private MsgType msgType;
    private String msgUser;
    private String msgBody;
    private String receivedString;

    public Message(String receivedString) {
        parse(receivedString);
        this.receivedString = receivedString;
    }
          
    public Message(MsgType type, String clientId, String body)
    {
        this.msgType = type;
        this.msgUser = clientId;
        this.msgBody = body;
    }

    public String getBody() {
        return msgBody;
    }
    public String getUser() {
        return msgUser;
    }
    public MsgType getType() {
        return msgType;
    }

    public String getReceivedString() {
        return receivedString;
    }
    
    @Override
    public String toString() {
        return "Message{" + "type=" + msgType + ", user=" + msgUser + ", body=" + msgBody + "}";
    }
   
    private void parse(String strToParse) {
        try {
            String[] msgTokens = strToParse.split("##");
            msgType = MsgType.valueOf(msgTokens[0].toUpperCase());
            if (hasUser(msgTokens)) {
                msgUser = msgTokens[1].trim();
            }
            if (hasBody(msgTokens)) {
                msgBody = msgTokens[2].trim();
            }
        } catch (Throwable throwable) {
            throw new MessageException(throwable);
        }
    }

    private boolean hasUser(String[] msgTokens) {
        return msgTokens.length > 1;
    }
    private boolean hasBody(String[] msgTokens) {
        return msgTokens.length > 2;
    }
        
}

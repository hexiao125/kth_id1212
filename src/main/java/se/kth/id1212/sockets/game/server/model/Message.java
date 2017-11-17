/*
 * The MIT License
 *
 * Copyright 2017 Leif Lindbäck <leifl@kth.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package se.kth.id1212.sockets.game.server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import se.kth.id1212.sockets.game.common.Constants;
import se.kth.id1212.sockets.game.common.MessageException;
import se.kth.id1212.sockets.game.common.MsgType;


/**
 * Holds the entire conversation, including all messages from all clients. All methods are thread
 * safe.
 */
public class Message {
    private String userName;
    private MsgType msgType;
    private String msgBody;
    private String receivedString;
    private boolean ownClient;

    public Message(String receivedString) {
        parse(receivedString);
        this.receivedString = receivedString;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserName() {
        return this.userName;
    }
        
    public MsgType getMsgType() {
        return this.msgType;
    }
   
    public String getMsgBody() {
        return this.msgBody;
    }
    
    public String getString() {
        return this.receivedString;
    }
            
    private void parse(String strToParse) {
        try {
            String[] msgTokens = strToParse.split(Constants.MSG_DELIMETER);
            msgType = MsgType.valueOf(msgTokens[Constants.MSG_TYPE_INDEX].toUpperCase());
            if (hasBody(msgTokens)) {
                msgBody = msgTokens[Constants.MSG_BODY_INDEX];
            }
        } catch (Throwable throwable) {
            throw new MessageException(throwable);
        }
    }

    public boolean hasBody(String[] msgTokens) {
        return msgTokens.length > 1;
    }

}

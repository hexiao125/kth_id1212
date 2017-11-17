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
import java.util.Observable;

// todo : observer
import se.kth.id1212.sockets.game.server.controller.Controller;
import se.kth.id1212.sockets.game.common.MessageException;

/**
 * Holds the game model, including all messages from all players. All methods are thread
 * safe.
 */
public class GameModel extends Observable {
    private final List<String> entries = Collections.synchronizedList(new ArrayList<>());

    private List<Observer> observers = new ArrayList<Observer>();
    private Controller contr;
    //
    private String msgToClient = new String();
    private boolean newRoundOpen = true; 
    private List<Player> players = new ArrayList<Player>();
    private enum ChoiceType {PAPER, ROCK, SCISSOR, UNDEFINED};
    private boolean updateUserInfo = false;
    private boolean updateScore = false;
    private boolean updateNewRound = false;
    private boolean gameRoundOver = false; 
    private boolean ownClientSet = false;
    private String ownClientName = new String();
    
    /**
     * Appends the specified entry to the game model.
     *
     * @param entry The entry to append.
     */
    public void appendEntry(String entry) {
        entries.add(entry);
    }

    /**
     * @return All entries in the game model, in the order they were entered.
     */
    public String[] getGameModel() {
        return entries.toArray(new String[0]);
    }
    
    /**
     * TODO
     * @param msg
     */
    
    public boolean checkUserExist(String newUserName){
        boolean nameUsed = false;
        for (Player p : players){
            if (newUserName.equals(p.getUserName())){
                nameUsed = true;
            }
        }
        return nameUsed;
    }
        
    public boolean checkOwnClientSet(){
        return this.ownClientSet;
    }
    
    public void setOwnClient(String userName){
        this.ownClientName = userName;
        this.ownClientSet = true;
    }
 
    public String getOwnClientName(){
        return this.ownClientName;
    }
        
    public void updateClientAction(Message msg) {
        
       gameArbitor(msg);
    }
    
    public String getMsgToClient() {
        return msgToClient;
    }    
    
    public void addObserver(Observer obs){
        observers.add(obs);
    }
    
    @Override
    public void notifyObservers(){
        for (Observer obs : observers){
            obs.update();
        }
    }
    
    // check if there is user info update
    public boolean checkUpdateUserInfo() {
        if (this.updateUserInfo) {
            this.updateUserInfo = false;
            return true;
        } else {
            return false;
        }
    }
    
    public void setUpdateUserInfo() {
            this.updateUserInfo = true;
    }
    
    // check if there is score update
    public boolean checkUpdateScore() {
        if (this.updateScore) {
            this.updateScore = false;
            return true;
        } else {
            return false;
        }
    } 
    
    public void setUpdateScore() {
            this.updateScore = true;
    }

        // check if there is score update
    public boolean checkUpdateNewRound() {
        if (this.updateNewRound) {
            this.updateNewRound = false;
            return true;
        } else {
            return false;
        }
    } 
    
    public void setUpdateNewRound() {
        this.updateNewRound = true;
    }
    
    public void gameArbitor(Message msg) {
        String username = msg.getUserName();
        String JOIN_MESSAGE = " joined game.";
        String LEAVE_MESSAGE = " left game.";
        String USERNAME_DELIMETER = ": ";
        msgToClient = new String();
        boolean allPlayerPerformed = true;
        int numPlayerGroupPaper = 0;
        int numPlayerGroupRock = 0;
        int numPlayerGroupScissor = 0;
        ChoiceType whichWins = ChoiceType.UNDEFINED;         
                
        switch (msg.getMsgType()) {
            case USER:
                Player p = new Player(username);
                players.add(p);
                msgToClient = username + JOIN_MESSAGE;
                // notify new player
                setUpdateUserInfo();
                notifyObservers();
                break;
            case ACTION:
                if (msg.getMsgBody().toUpperCase().equals("PLAY")) {
                    for (Player pl : players) {
                        if (pl.userName.equals(username)) {
                            pl.setReadyToPlay();
                        }
                    }
                    // clear last round score
                    if (this.gameRoundOver){
                        this.gameRoundOver = false;
                        resetLastRoundScore();
                        setUpdateNewRound();
                        notifyObservers();
                    }
                    
                } else {
                    // write players choice to database
                    for (Player pl : players) {
                        if (pl.userName.equals(username) && pl.readyToPlay && !pl.performed) {
                            if (pl.setChoice(msg.getMsgBody())) {
                                pl.setPerformed();
                            }
                        }
                    }
                }

                // check game status
                for (Player pl : players) {
                    if (!pl.performed) {
                        allPlayerPerformed = false;
                    }
                    switch (pl.getChoice()){
                        case PAPER:
                            numPlayerGroupPaper++;
                            break;
                        case ROCK:
                            numPlayerGroupRock++;
                            break;
                        case SCISSOR:
                            numPlayerGroupScissor++;
                            break;
                        default:
                    }
                }
                if (allPlayerPerformed) {
                    // arbitrate the game
                    if(numPlayerGroupPaper!=0 && numPlayerGroupRock!=0 && numPlayerGroupScissor==0){
                        whichWins = ChoiceType.PAPER;
                    } else if (numPlayerGroupPaper!=0 && numPlayerGroupRock==0 && numPlayerGroupScissor!=0){ 
                        whichWins = ChoiceType.SCISSOR;
                    } else if (numPlayerGroupPaper==0 && numPlayerGroupRock!=0 && numPlayerGroupScissor!=0){ 
                        whichWins = ChoiceType.ROCK;
                    } else {
                        whichWins = ChoiceType.UNDEFINED; // draw game
                    }
   
                    // set score and flush last round
                    for (Player pl : players) {
                        switch (whichWins) {
                            case PAPER: 
                                pl.setScoreAndFlushLastRound(ChoiceType.PAPER, numPlayerGroupRock);
                                break;
                            case ROCK: 
                                pl.setScoreAndFlushLastRound(ChoiceType.ROCK, numPlayerGroupScissor);
                                break;
                            case SCISSOR: 
                                pl.setScoreAndFlushLastRound(ChoiceType.SCISSOR, numPlayerGroupPaper);    
                                break;
                            default:
                                pl.setScoreAndFlushLastRound(ChoiceType.UNDEFINED, 0);
                        }
                    }
                    // notify the end of a round
                    this.gameRoundOver = true;
                    setUpdateScore();
                    notifyObservers();
                } else {
                    msgToClient = "Waiting for all players to perform!";
                }
                break;
            case DISCONNECT:
                //disconnectClient();
                for (Player p1 : players){
                    if (p1.getUserName().equals(username)){
                        players.remove(p1);
                    }
                }
                msgToClient = username + LEAVE_MESSAGE;
                // notify player left
                setUpdateUserInfo();
                notifyObservers();
                break;
            default:
                throw new MessageException("Received corrupt message: " + msg.getString());
        }
    }
    
    public boolean getReadyToPlay(String username) {
        for (Player pl : players) {
            if (pl.getUserName().equals(username)){
                return pl.readyToPlay;
            }
        }
        return false;
    }
        
    public String getGameResult(String username) {
        String s = new String();
        for (Player pl : players) {
            if (pl.getUserName().equals(username)) {
                s = pl.gameResult;
                break;
            }
        }
        return s;
    }

    public int getLastScore(String username) {
        int score = 0;
        for (Player pl : players) {
            if (pl.getUserName().equals(username)) {
                score = pl.getLastScore();
                break;
            }
        }
        return score;
    }

    public int getTotalScore(String username) {
        int score = 0;
        for (Player pl : players) {
            if (pl.getUserName().equals(username)) {
                score = pl.getTotalScore();
                break;
            }
        }
        return score;
    }
        
    public void resetLastRoundScore() {
        for (Player pl : players) {
            pl.resetLastRoundScore();
        }
    }
        
    // private class Player
    private class Player {
        private final String userName;
        private boolean readyToPlay;
        private boolean performed;
        private ChoiceType choice;
        private int scoreTotal;
        private int scoreLast;
        private String gameResult = new String();

        public Player(String userName) {
            this.userName = userName;
            this.readyToPlay = false;
            this.performed = false;
            this.scoreTotal = 0;
            this.scoreLast = 0;
            this.choice = ChoiceType.UNDEFINED;
        }

        public boolean setChoice(String str) {
            switch (str.toUpperCase()) {
                case "PAPER":
                    this.choice = ChoiceType.PAPER;
                    return true;
                case "ROCK":
                    this.choice = ChoiceType.ROCK;
                    return true;
                case "SCISSOR":
                    this.choice = ChoiceType.SCISSOR;
                    return true;
                default:       
                    return false;
            }
        }
          
        public void setPerformed() {
            this.performed = true;
        }
        
        public void setReadyToPlay() {
            this.readyToPlay = true;
        }
                
        public void setScoreAndFlushLastRound(ChoiceType whichWins, int score) {
            if (whichWins == ChoiceType.UNDEFINED){
                gameResult = "Draw game!";
            } else if (this.choice.equals(whichWins)){
                this.scoreLast = score;
                this.scoreTotal = this.scoreTotal + score;
                gameResult = "You Win!";
            } else {
                gameResult = "You Lose!";
            }
            this.readyToPlay = false;
            this.performed = false;
            this.choice = ChoiceType.UNDEFINED;
        }
                 
        public void resetLastRoundScore() {
            this.scoreLast = 0;
        }
             
        public String getUserName() {
            return this.userName;
        }
               
        public boolean getPerformed() {
            return this.performed;
        }
        
        public ChoiceType getChoice() {
            return this.choice;
        }
        
        public String getGameResult() {
            return this.gameResult;
        }               
        public int getLastScore() {
            return this.scoreLast;
        }
        
        public int getTotalScore() {
            return this.scoreTotal;
        }
         
    }
}

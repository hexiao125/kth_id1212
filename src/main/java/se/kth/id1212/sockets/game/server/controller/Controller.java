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
package se.kth.id1212.sockets.game.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import se.kth.id1212.sockets.game.server.model.GameModel;
import se.kth.id1212.sockets.game.server.model.Message;
import se.kth.id1212.sockets.game.server.model.Observer;

// todo observer

/**
 * The server side controller. All calls to the server side model pass through here.
 */
public class Controller extends Observable implements Observer {
    private final GameModel gameModel = new GameModel();

    // todo observer
    private List<Observer> observers = new ArrayList<Observer>();
    private String msgToClient = new String();
    
    public Controller(){
        this.gameModel.addObserver(this);
    }
    /**
     * Appends the specified entry to the game model.
     *
     * @param entry The entry to append.
     */
    public void appendEntry(String entry) {
        gameModel.appendEntry(entry);
    }

    /**
     * @return All entries in the game model, in the order they were entered.
     */
    public String[] getGameModel() {
        return gameModel.getGameModel();
    }
   
     /**
     * TODO
     */
    public void addObserver(Observer obs){
        observers.add(obs);
    }
         
    @Override
    public void notifyObservers(){
        for (Observer obs : observers){
            obs.update();
        }
    }
    
    public void updateClientAction(Message msg) {
        gameModel.updateClientAction(msg);
    }
    
    public void broadcast(String msg) {
        
    }
      
    @Override
    public void update(){
        msgToClient = this.gameModel.getMsgToClient();
        notifyObservers();
    }
 
    // check if there is user info update
    public boolean checkUpdateUserInfo() {
        return this.gameModel.checkUpdateUserInfo();
    }
    
    public boolean checkUpdateScore() {
         return this.gameModel.checkUpdateScore();
    }
          
    public boolean checkUpdateNewRound() {
         return this.gameModel.checkUpdateNewRound();
    }
    
    public boolean getReadyToPlay(String username){
        return this.gameModel.getReadyToPlay(username);
    }
    
    public String getGameResult(String username) {
        return this.gameModel.getGameResult(username);
    }
    
    public int getLastScore(String username){
        return this.gameModel.getLastScore(username);
    }
    
    public int getTotalScore(String username){
        return this.gameModel.getTotalScore(username);
    }
    
    public String getMsgToClient() {
        return msgToClient;
    }
       
    public boolean checkUserExist(String newUserName){  
        return this.gameModel.checkUserExist(newUserName);
    }
    
    public boolean checkOwnClientSet(){
        return this.gameModel.checkOwnClientSet();
    }
    
    public void setOwnClient(String userName){
        this.gameModel.setOwnClient(userName);
    }
        
    public String getOwnClientName(){
        return this.gameModel.getOwnClientName();
    }
}

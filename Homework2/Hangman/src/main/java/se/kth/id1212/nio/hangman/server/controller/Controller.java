package se.kth.id1212.nio.hangman.server.controller;

import se.kth.id1212.nio.hangman.common.Message;
import se.kth.id1212.nio.hangman.server.model.GameCenter;

public class Controller {
    private final GameCenter gameModel;
    
    public Controller() {
        this.gameModel = new GameCenter();
    }

    public Message startGame(String clientId) {
        gameModel.startGame(clientId);
        return gameModel.updateResult(clientId);
    }
    
    public void createClient(String clientId) {
        gameModel.createClient(clientId);
    }
    
    public Message checkPlayerGuess(Message msg) {
        return gameModel.checkPlayerGuess(msg);
    }
    
    public int getScore(String clientId) {
        return gameModel.getScore(clientId);
    }
    
    
}

package se.kth.id1212.sockets.hangman.server.controller;

import se.kth.id1212.sockets.hangman.common.Message;
import se.kth.id1212.sockets.hangman.server.model.GameCenter;

public class Controller {

    private GameCenter gameModel;

    public Controller() {
        this.gameModel = new GameCenter();
    }

    public Message startGame(int clientId) {
        gameModel.startGame(clientId);
        return gameModel.updateResult(clientId);
    }

    public Message checkPlayerGuess(String input, int clientId) {
        return gameModel.checkPlayerGuess(input, clientId);
    }
    
    public int getScore(int clientId) {
        return gameModel.getScore(clientId);
    }
    
}

package se.kth.id1212.sockets.hangman.server.model;

import se.kth.id1212.sockets.hangman.common.Message;
import java.util.HashMap;
import se.kth.id1212.sockets.hangman.server.integration.WordBank;

public class GameCenter {

    private final HashMap<Integer, GameModel> clientStatus = new HashMap<>(); 
    private final WordBank wordBank;


    public GameCenter() {
        this.wordBank = new WordBank();
    }

    public void startGame(int clientId) {
        // create new client if not exist
        if (!this.clientStatus.containsKey(clientId)){
            this.clientStatus.put(clientId, new GameModel());
        }
        String genWord = this.wordBank.getRandomWord().toLowerCase();
        this.clientStatus.get(clientId).genNewWord(genWord);
    }

    public Message checkPlayerGuess(String guess, int clientId) {
        return this.clientStatus.get(clientId).checkPlayerGuess(guess);
    }

    public Message updateResult(int clientId) {
        return this.clientStatus.get(clientId).updateResult();
    }
    
    public int getScore(int clientId){
        return this.clientStatus.get(clientId).getScore();
    }
    
}

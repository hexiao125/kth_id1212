package se.kth.id1212.nio.hangman.server.model;

import se.kth.id1212.nio.hangman.common.Message;
import java.util.HashMap;
import se.kth.id1212.nio.hangman.server.integration.WordBank;

public class GameCenter {

    private final HashMap<String, GameModel> clientStatus = new HashMap<>(); 
    private final WordBank wordBank;


    public GameCenter() {
        this.wordBank = new WordBank();
    }

   public void createClient(String clientId) {
        // create new client if not exist
        if (!this.clientStatus.containsKey(clientId)){
            this.clientStatus.put(clientId, new GameModel(clientId));
        }
    }
        
    public void startGame(String clientId) {
        String genWord = this.wordBank.getRandomWord().toLowerCase();
        this.clientStatus.get(clientId).genNewWord(genWord);
    }

    public Message checkPlayerGuess(Message msg) {
        String guess = msg.getBody();
        String clientId = msg.getUser();
        return this.clientStatus.get(clientId).checkPlayerGuess(guess);
    }

    public Message updateResult(String clientId) {
        return this.clientStatus.get(clientId).updateResult();
    }
    
    public int getScore(String clientId){
        return this.clientStatus.get(clientId).getScore();
    }
    
}

package se.kth.id1212.nio.hangman.server.model;

import se.kth.id1212.nio.hangman.common.Message;
import se.kth.id1212.nio.hangman.common.MsgType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameModel {

    private static final String GAME_LOST_MESSAGE = "You lose! The correct word was: ";
    private static final String GAME_WON_MESSAGE = "You win! You got the correct word: ";
    private static final String YOUR_SCORE = "Your current score is: ";
    private static final String INSTRUCTIONS = "Enter <start> to start a new game, or <quit> to quit the game.";
    private String wordHint;
    private String generatedWord;
    private int numTrialLeft;
    private final List<Character> guessedLetters = new ArrayList<>();
    private int score;
    private String clientId;

    public GameModel(String clientId) {
        this.clientId = clientId;
        this.score = 0;
    }

    public void genNewWord(String generatedWord) {
        this.generatedWord = generatedWord;
        this.numTrialLeft = generatedWord.length();
        this.guessedLetters.clear();
    }

    public Message checkPlayerGuess(String guess) {
        Message result;
        if (guess.matches("[a-zA-Z]{1}")) {
            result = checkSingleLetter(guess);
        } else if (guess.length() == generatedWord.length() && guess.matches("[a-zA-Z]+")) {
            result = checkEntireWord(guess);
        } else {
            result = updateResult();
        }
        return result;
    }

    private Message checkSingleLetter(String guess) {
        char letter = guess.toLowerCase().charAt(0);
        if (guessedLetters.contains(letter)) { // letter is guessed
            return updateResult();
        } else { // new guessed letter
            guessedLetters.add(letter);
            Collections.sort(guessedLetters);
            if (!generatedWord.contains(String.valueOf(letter))) {
                this.numTrialLeft--;
            }
        }
        // check result
        if (allLettersMatch()) {
            return gameOver(true);
        } else if (numTrialLeft <= 0) {
            return gameOver(false);
        } else {
            return updateResult();
        }
    }

    private Message checkEntireWord(String guess) {
        if (guess.equalsIgnoreCase(generatedWord)) {
            return gameOver(true);
        } else if (numTrialLeft <= 0) {
            return gameOver(false);
        } else {
            this.numTrialLeft--;
            return updateResult();
        }
    }

    public Message updateResult() {
        //createHints
        this.wordHint = "";
        for (int i = 0; i < generatedWord.length(); i++) {
            if (guessedLetters.contains(generatedWord.charAt(i))) {
                this.wordHint += (generatedWord.charAt(i) + " ");
            } else {
                this.wordHint += "_ ";
            }
        }
        // display guessed letter
        this.wordHint += ("\nYour guessed letters were [");
        for (Character letter : guessedLetters) {
            this.wordHint += (letter + " ");
        }
        this.wordHint += ("]\nRemaining number of guess is: " + this.numTrialLeft + "\n");
        return new Message(MsgType.RESULT, this.clientId, this.wordHint);
    }

    private boolean allLettersMatch() {
        for (int i = 0; i < generatedWord.length(); i++) {
            if (!(guessedLetters.contains(generatedWord.charAt(i)))) {
                return false;
            }
        }
        return true;
    }
    
    private Message gameOver(boolean win) {
        guessedLetters.clear();
        if (win) {
            this.score++;   
            return new Message(MsgType.GAMEOVER, this.clientId, 
                    GAME_WON_MESSAGE + generatedWord + "\n> " 
                    + YOUR_SCORE + String.valueOf(this.getScore()) + "\n> "
                    + INSTRUCTIONS);
        } else {
            this.score--;            
            return new Message(MsgType.GAMEOVER, this.clientId, 
                    GAME_LOST_MESSAGE + generatedWord + "\n> " 
                    + YOUR_SCORE + String.valueOf(this.getScore()) + "\n> "
                    + INSTRUCTIONS);
        }
    }
    
    public int getScore(){
        return this.score;
    }
    
}

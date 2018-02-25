package se.kth.id1212.nio.hangman.server.integration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordBank {
    
    private static final String FILENAME = "src/main/java/se/kth/id1212/nio/hangman/server/integration/words.txt";
    private static final File WORDFILE = new File(FILENAME);
    private final List<String> wordList = new ArrayList<>();

    private void loadWords() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(WORDFILE));
            String line = reader.readLine();
            while(line != null){
                this.wordList.add(line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException fileNotFound) {
            System.err.println("File not found: " + fileNotFound);
        } catch (IOException ioe) {
            System.err.println("I/O exception: " + ioe);
        }
    }

    public String getRandomWord() {
        if (wordList.isEmpty()) {
            loadWords();
        }
        Random rand = new Random();
        String randomWord = wordList.get(rand.nextInt(wordList.size()));
        return randomWord;
    }
}

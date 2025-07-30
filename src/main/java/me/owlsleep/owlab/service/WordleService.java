package me.owlsleep.owlab.service;

import jakarta.annotation.PostConstruct;
import me.owlsleep.owlab.entity.Word;
import me.owlsleep.owlab.model.Tile;
import me.owlsleep.owlab.model.WordleState;
import me.owlsleep.owlab.repository.WordRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class WordleService {

    private final WordRepository wordRepository;
    private WordleState wordleState;

    public WordleService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @PostConstruct
    public void init() {
        resetWordle();
    }

    public void resetWordle() {
        List<Word> allWords = wordRepository.findAll();
        String answer = allWords.get(new Random().nextInt(allWords.size())).getWord();
        wordleState = new WordleState(answer);
    }

    public WordleState getWordleState() {
        return wordleState;
    }

    public boolean isValidWord(String word) {
        return wordRepository.findByWordIgnoreCase(word).isPresent();
    }

    public List<List<Tile>> getTiles() {
        List<List<Tile>> result = new ArrayList<>();
        String answer = wordleState.getAnswer();

        for (String guess : wordleState.getGuesses()) {
            List<Tile> row = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
                char gChar = guess.charAt(i);
                char aChar = answer.charAt(i);

                String color;
                if (gChar == aChar) {
                    color = "green";
                } else if (answer.indexOf(gChar) != -1) {
                    color = "yellow";
                } else {
                    color = "gray";
                }

                row.add(new Tile(gChar, color));
            }

            result.add(row);
        }

        return result;
    }
}

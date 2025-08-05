package me.owlsleep.owlab.service;

import me.owlsleep.owlab.repository.WordRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class WordleService {

    private final WordRepository wordRepository;
    private final Random random = new Random();

    public WordleService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    public String getRandomWord() {
        return wordRepository.findRandomWord().toUpperCase();
    }

    public boolean exists(String word) {
        return wordRepository.existsByWord(word);
    }

    public List<String> checkGuess(String guess, String target) {
        List<String> result = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            char g = guess.charAt(i);
            if (g == target.charAt(i)) {
                result.add("G"); // 초록
            } else if (target.indexOf(g) >= 0) {
                result.add("Y"); // 노랑
            } else {
                result.add("B"); // 회색
            }
        }
        return result;
    }
}
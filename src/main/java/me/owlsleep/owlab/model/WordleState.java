package me.owlsleep.owlab.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class WordleState {

    private final String answer;
    private final List<String> guesses = new ArrayList<>();

    public WordleState(String answer) {
        this.answer = answer.toUpperCase();
    }

    public void addGuess(String guess) {
        guesses.add(guess.toUpperCase());
    }
}


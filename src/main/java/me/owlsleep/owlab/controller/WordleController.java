package me.owlsleep.owlab.controller;

import me.owlsleep.owlab.service.WordleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/wordle")
public class WordleController {

    private final WordleService wordleService;

    public WordleController(WordleService wordleService) {
        this.wordleService = wordleService;
    }

    @GetMapping
    public String wordle(Model model) {
        model.addAttribute("wordleState", wordleService.getWordleState());
        return "wordle";
    }

    @PostMapping("/guess")
    public String guess(@RequestParam String guess, Model model) {
        if (wordleService.isValidWord(guess)) {
            wordleService.getWordleState().addGuess(guess);
            model.addAttribute("tiles", wordleService.getTiles());
        }
        return "wordle";
    }
}
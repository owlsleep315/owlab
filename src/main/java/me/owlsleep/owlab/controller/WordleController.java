package me.owlsleep.owlab.controller;

import me.owlsleep.owlab.service.WordleService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/wordle")
public class WordleController {

    private final WordleService wordleService;
    private String targetWord;
    private int attempts;

    public WordleController(WordleService wordleService) {
        this.wordleService = wordleService;
    }

    @GetMapping
    public String wordle(Model model) {
        targetWord = wordleService.getRandomWord();
        attempts = 0;
        model.addAttribute("message", "새 게임 시작");
        return "wordle";
    }

    @PostMapping("/start")
    @ResponseBody
    public Map<String, String> startGame() {
        targetWord = wordleService.getRandomWord();
        attempts = 0;
        return Map.of("message", "새 게임 시작");
    }


    @PostMapping("/guess")
    @ResponseBody
    public Object guess(@RequestBody Map<String, String> payload) {
        if (targetWord == null) {
            return Map.of("error", "게임이 시작되지 않았습니다.");
        }

        String guess = payload.get("guess").toUpperCase();

        if (!wordleService.exists(guess)) {
            return Map.of("error", "존재하지 않는 단어입니다.");
        }

        attempts++;
        List<String> result = wordleService.checkGuess(guess, targetWord);

        // 정답 맞춘 경우
        if (guess.equals(targetWord)) {
            return Map.of("result", result, "success", true);
        }

        // 기회를 모두 소진한 경우
        if (attempts >= 6) {
            return Map.of("result", result, "success", false, "answer", targetWord);
        }

        // 아직 게임 진행 중
        return Map.of("result", result);
    }
}
package me.owlsleep.owlab.controller;

import jakarta.servlet.http.HttpSession;
import me.owlsleep.owlab.entity.User;
import me.owlsleep.owlab.service.WordleService;
import me.owlsleep.owlab.service.WordleStatisticService;
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
    private final WordleStatisticService wordleStatisticService;

    private static final String SESSION_TARGET_WORD = "wordleTargetWord";
    private static final String SESSION_ATTEMPTS = "wordleAttempts";

    public WordleController(WordleService wordleService, WordleStatisticService wordleStatisticService) {
        this.wordleService = wordleService;
        this.wordleStatisticService = wordleStatisticService;
    }

    @GetMapping
    public String wordle(Model model, HttpSession session) {
        startNewGame(session);
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser != null) {
            wordleStatisticService.recordGameResult(loginUser, false);
        }
        model.addAttribute("message", "새 게임 시작");
        return "wordle";
    }

    @PostMapping("/start")
    @ResponseBody
    public Map<String, String> startGame(HttpSession session) {
        startNewGame(session);
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser != null) {
            wordleStatisticService.recordGameResult(loginUser, false);
        }
        return Map.of("message", "새 게임 시작");
    }


    @PostMapping("/guess")
    @ResponseBody
    public Object guess(@RequestBody Map<String, String> payload, HttpSession session) {
        String targetWord = (String) session.getAttribute(SESSION_TARGET_WORD);
        if (targetWord == null) {
            return Map.of("error", "게임이 시작되지 않았습니다.");
        }

        String guess = payload.get("guess").toUpperCase();

        if (!wordleService.exists(guess)) {
            return Map.of("error", "존재하지 않는 단어입니다.");
        }

        int attempts = incrementAttempts(session);
        List<String> result = wordleService.checkGuess(guess, targetWord);

        User loginUser = (User) session.getAttribute("loginUser");

        // 정답 맞춘 경우
        if (guess.equals(targetWord)) {
            if (loginUser != null) {
                wordleStatisticService.recordGameResult(loginUser, true);
            }
            resetGame(session);
            return Map.of("result", result, "success", true);
        }

        // 기회를 모두 소진한 경우
        if (attempts >= 6) {
            String answer = targetWord;
            resetGame(session);
            return Map.of("result", result, "success", false, "answer", answer);
        }

        // 아직 게임 진행 중
        return Map.of("result", result);
    }

    @GetMapping("/ranking")
    public String ranking(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        model.addAttribute("rankings", wordleStatisticService.getTopRankings(loginUser));
        if (loginUser != null) {
            model.addAttribute("myRanking", wordleStatisticService.getRankingSnapshot(loginUser));
        }
        return "wordle-ranking";
    }

    private void startNewGame(HttpSession session) {
        session.setAttribute(SESSION_TARGET_WORD, wordleService.getRandomWord());
        session.setAttribute(SESSION_ATTEMPTS, 0);
    }

    private int incrementAttempts(HttpSession session) {
        Integer attempts = (Integer) session.getAttribute(SESSION_ATTEMPTS);
        if (attempts == null) {
            attempts = 0;
        }
        attempts++;
        session.setAttribute(SESSION_ATTEMPTS, attempts);
        return attempts;
    }

    private void resetGame(HttpSession session) {
        session.removeAttribute(SESSION_TARGET_WORD);
        session.removeAttribute(SESSION_ATTEMPTS);
    }
}
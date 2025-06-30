package me.owlsleep.owlab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/board")
    public String board() {
        return "board";
    }

    @GetMapping("/wordle")
    public String wordle() {
        return "wordle";
    }

    @GetMapping("/food")
    public String food() {
        return "food";
    }
}
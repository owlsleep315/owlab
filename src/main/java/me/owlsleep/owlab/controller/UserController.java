package me.owlsleep.owlab.controller;

import jakarta.servlet.http.HttpSession;
import me.owlsleep.owlab.dto.request.LoginRequestDto;
import me.owlsleep.owlab.dto.request.RegisterRequestDto;
import me.owlsleep.owlab.entity.User;
import me.owlsleep.owlab.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequestDto loginRequestDto,
                        HttpSession session,
                        Model model) {
        try {
            User user = userService.login(loginRequestDto.getUsername(), loginRequestDto.getPassword());
            session.setAttribute("loginUser", user);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequestDto registerRequestDto,
                           Model model) {
        try {
            userService.register(registerRequestDto.getName(), registerRequestDto.getUsername(), registerRequestDto.getPassword());
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @ResponseBody
    @GetMapping("/check-username")
    public boolean checkUsername(@RequestParam String username) {
        return !userService.existsByUsername(username);
    }
}


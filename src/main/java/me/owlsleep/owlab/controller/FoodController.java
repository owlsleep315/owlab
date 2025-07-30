package me.owlsleep.owlab.controller;

import lombok.RequiredArgsConstructor;
import me.owlsleep.owlab.entity.Food;
import me.owlsleep.owlab.service.FoodService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    // 메뉴추천 페이지 보여주기
    @GetMapping("/food")
    public String showMenuPage() {
        return "food";
    }

    // 메뉴추천받기 버튼 눌렀을 때
    @PostMapping("/food/random")
    public String getRandomMenu(Model model) {
        Food randomFood = foodService.getRandomFood();
        model.addAttribute("randomFood", randomFood);
        return "food";
    }
}

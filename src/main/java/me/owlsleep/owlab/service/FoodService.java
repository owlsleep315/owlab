package me.owlsleep.owlab.service;

import lombok.RequiredArgsConstructor;
import me.owlsleep.owlab.entity.Food;
import me.owlsleep.owlab.repository.FoodRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;

    public Food getRandomFood() {
        return foodRepository.findRandomFood();
    }
}

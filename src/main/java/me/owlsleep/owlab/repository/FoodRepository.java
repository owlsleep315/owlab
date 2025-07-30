package me.owlsleep.owlab.repository;

import me.owlsleep.owlab.entity.Food;
import me.owlsleep.owlab.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {

    // MySQL 랜덤 하나 추출
    @Query(value = "SELECT * FROM foods ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Food findRandomFood();
}

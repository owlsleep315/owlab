package me.owlsleep.owlab.repository;

import me.owlsleep.owlab.dto.GameDto;
import me.owlsleep.owlab.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findByGameName(String gameName);

    // 상위 5개
    List<Game> findTop5ByOrderByViewsDescIdDesc();

    // 검색
    List<Game> findByGameNameContainingIgnoreCaseOrderByViewsDesc(String keyword);
}
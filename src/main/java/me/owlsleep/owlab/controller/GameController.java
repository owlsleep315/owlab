package me.owlsleep.owlab.controller;

import lombok.RequiredArgsConstructor;
import me.owlsleep.owlab.dto.GameDto;
import me.owlsleep.owlab.dto.request.GameCreateRequestDto;
import me.owlsleep.owlab.dto.response.GameResponseDto;
import me.owlsleep.owlab.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    // 게임 등록
    @PostMapping
    public ResponseEntity<GameDto> create(@RequestBody GameCreateRequestDto dto) {
        GameDto response = gameService.createGame(dto);
        return ResponseEntity.created(URI.create("/api/games/" + response.getId())).body(response);
    }

    // 조회수 상위 5개
    @GetMapping
    public ResponseEntity<List<GameResponseDto>> getTopGames() {
        return ResponseEntity.ok(gameService.getTopGames());
    }

    // 게임 상세
    @GetMapping("/{id}")
    public ResponseEntity<GameDto> getGameDetail(@PathVariable Long id) {
        return ResponseEntity.ok(gameService.getGameDetail(id));
    }

    // 검색
    @GetMapping("/search")
    public ResponseEntity<List<GameResponseDto>> searchGames(@RequestParam String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return ResponseEntity.ok(gameService.getTopGames());
        }
        return ResponseEntity.ok(gameService.searchGames(keyword));
    }

    // 관리자: 오피셜 플래그 토글
    @PatchMapping("/{id}/official")
    public ResponseEntity<GameResponseDto> official(@PathVariable Long id, @RequestParam boolean value) {
        return  ResponseEntity.ok(gameService.setOfficial(id, value));
    }
}

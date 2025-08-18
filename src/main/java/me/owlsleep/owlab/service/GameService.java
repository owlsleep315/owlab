package me.owlsleep.owlab.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.owlsleep.owlab.dto.GameDto;
import me.owlsleep.owlab.dto.request.GameCreateRequestDto;
import me.owlsleep.owlab.dto.response.GameResponseDto;
import me.owlsleep.owlab.entity.Game;
import me.owlsleep.owlab.repository.GameRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;

    // 게임 등록
    public GameDto createGame(GameCreateRequestDto dto) {
        gameRepository.findByGameName(dto.getGameName())
                .ifPresent(g -> { throw new IllegalArgumentException("이미 존재하는 게임입니다."); });

        Game game = Game.builder()
                .gameName(dto.getGameName())
                .couponUsage(dto.getCouponUsage())
                .couponLink(dto.getCouponLink())
                .imageUrl(dto.getImageUrl())
                .views(0)
                .official(false)
                .createdAt(LocalDateTime.now())
                .build();
        Game saved = gameRepository.save(game);
        return toDto(saved);
    }

    // 상위 5개 게임
    public List<GameResponseDto> getTopGames() {
        return gameRepository.findTop5ByOrderByViewsDescIdDesc()
                .stream().map(this::toResponseDto).toList();
    }

    // 게임 상세 조회 (조회수 증가)
    @Transactional
    public GameDto getGameDetail(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게임을 찾을 수 없습니다."));
        game.setViews(game.getViews() + 1);
        return toDto(gameRepository.save(game));
    }

    // 게임 검색
    public List<GameResponseDto> searchGames(String keyword) {
        return gameRepository.findByGameNameContainingIgnoreCaseOrderByViewsDesc(keyword)
                .stream().map(this::toResponseDto).toList();
    }

    // 관리자: 오피셜 전환/해제
    @Transactional
    public GameResponseDto setOfficial(Long id, boolean official) {
        Game game = gameRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        game.setOfficial(official);
        return toResponseDto(gameRepository.save(game));
    }

    private GameResponseDto toResponseDto(Game g) {
        return GameResponseDto.builder()
                .id(g.getId()).gameName(g.getGameName())
                .imageUrl(g.getImageUrl()).views(g.getViews())
                .official(g.isOfficial()).build();
    }

    private GameDto toDto(Game g) {
        return GameDto.builder()
                .id(g.getId())
                .gameName(g.getGameName())
                .couponUsage(g.getCouponUsage())
                .couponLink(g.getCouponLink())
                .imageUrl(g.getImageUrl())
                .views(g.getViews())
                .official(g.isOfficial())
                .createdAt(g.getCreatedAt())
                .build();
    }
}

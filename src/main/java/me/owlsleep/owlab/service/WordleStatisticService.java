package me.owlsleep.owlab.service;

import lombok.RequiredArgsConstructor;
import me.owlsleep.owlab.dto.response.WordleRankingDto;
import me.owlsleep.owlab.entity.User;
import me.owlsleep.owlab.entity.WordleStatistic;
import me.owlsleep.owlab.repository.WordleStatisticRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WordleStatisticService {

    private final WordleStatisticRepository wordleStatisticRepository;

    @Transactional
    public void recordGameResult(User user, boolean success) {
        if (user == null) {
            return;
        }
        WordleStatistic statistic = wordleStatisticRepository.findByUser(user)
                .orElseGet(() -> WordleStatistic.builder()
                        .user(user)
                        .totalGames(0L)
                        .successCount(0L)
                        .build());

        statistic.setTotalGames(statistic.getTotalGames() + 1);
        if (success) {
            statistic.setSuccessCount(statistic.getSuccessCount() + 1);
        }
        wordleStatisticRepository.save(statistic);
    }

    public List<WordleRankingDto> getTopRankings(User currentUser) {
        List<WordleStatistic> statistics = wordleStatisticRepository.findTop10ByOrderBySuccessCountDescTotalGamesAscIdAsc();
        return toRankingDtos(statistics, currentUser);
    }

    public WordleRankingDto getRankingSnapshot(User user) {
        if (user == null) {
            return null;
        }

        Optional<WordleStatistic> optionalStatistic = wordleStatisticRepository.findByUser(user);
        if (optionalStatistic.isEmpty()) {
            return createEmptyRanking(user);
        }

        WordleStatistic statistic = optionalStatistic.get();
        List<WordleStatistic> allStatistics = wordleStatisticRepository.findAllByOrderBySuccessCountDescTotalGamesAscIdAsc();
        int rank = calculateRank(allStatistics, statistic);
        return toRankingDto(statistic, rank, true);
    }

    private List<WordleRankingDto> toRankingDtos(List<WordleStatistic> statistics, User currentUser) {
        List<WordleRankingDto> rankings = new ArrayList<>();
        long previousSuccess = Long.MIN_VALUE;
        long previousGames = Long.MIN_VALUE;
        int currentRank = 0;

        for (int i = 0; i < statistics.size(); i++) {
            WordleStatistic statistic = statistics.get(i);
            if (statistic.getSuccessCount() != previousSuccess || statistic.getTotalGames() != previousGames) {
                currentRank = i + 1;
                previousSuccess = statistic.getSuccessCount();
                previousGames = statistic.getTotalGames();
            }
            boolean mine = currentUser != null && statistic.getUser().getId().equals(currentUser.getId());
            rankings.add(toRankingDto(statistic, currentRank, mine));
        }

        return rankings;
    }

    private int calculateRank(List<WordleStatistic> statistics, WordleStatistic target) {
        long previousSuccess = Long.MIN_VALUE;
        long previousGames = Long.MIN_VALUE;
        int currentRank = 0;

        for (int i = 0; i < statistics.size(); i++) {
            WordleStatistic statistic = statistics.get(i);
            if (statistic.getSuccessCount() != previousSuccess || statistic.getTotalGames() != previousGames) {
                currentRank = i + 1;
                previousSuccess = statistic.getSuccessCount();
                previousGames = statistic.getTotalGames();
            }
            if (statistic.getId().equals(target.getId())) {
                return currentRank;
            }
        }
        return statistics.size() + 1;
    }

    private WordleRankingDto toRankingDto(WordleStatistic statistic, Integer rank, boolean mine) {
        return WordleRankingDto.builder()
                .rank(rank)
                .displayName(statistic.getUser().getUsername())
                .successCount(statistic.getSuccessCount())
                .totalGames(statistic.getTotalGames())
                .winRate(calculateWinRate(statistic))
                .mine(mine)
                .build();
    }

    private WordleRankingDto createEmptyRanking(User user) {
        return WordleRankingDto.builder()
                .rank(null)
                .displayName(user.getUsername())
                .successCount(0L)
                .totalGames(0L)
                .winRate(0.0)
                .mine(true)
                .build();
    }

    private double calculateWinRate(WordleStatistic statistic) {
        if (statistic.getTotalGames() == 0) {
            return 0.0;
        }
        double rate = (double) statistic.getSuccessCount() / statistic.getTotalGames() * 100.0;
        return Math.round(rate * 10.0) / 10.0;
    }
}

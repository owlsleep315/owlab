package me.owlsleep.owlab.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WordleRankingDto {
    private final Integer rank;
    private final String displayName;
    private final long successCount;
    private final long totalGames;
    private final double winRate;
    private final boolean mine;
}

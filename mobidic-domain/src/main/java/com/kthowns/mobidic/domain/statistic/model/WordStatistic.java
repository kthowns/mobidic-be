package com.kthowns.mobidic.domain.statistic.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record WordStatistic(
        UUID wordId,
        long correctCount,
        long incorrectCount,
        boolean isLearned,
        double difficulty,
        double accuracy
) {
}


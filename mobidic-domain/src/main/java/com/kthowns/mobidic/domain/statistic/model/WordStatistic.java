package com.kthowns.mobidic.domain.statistic.model;

import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WordStatistic {
    private UUID wordId;
    private Long correctCount;
    private Long incorrectCount;
    private boolean isLearned;
    private double difficulty;
    private double accuracy;
}


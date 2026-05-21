package com.kthowns.mobidic.domain.statistic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
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

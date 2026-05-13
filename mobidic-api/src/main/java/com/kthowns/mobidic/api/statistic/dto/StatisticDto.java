package com.kthowns.mobidic.api.statistic.dto;

import com.kthowns.mobidic.api.statistic.entity.WordStatistic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatisticDto {
    private UUID wordId;
    private Long correctCount;
    private Long incorrectCount;
    private boolean isLearned;
    private double difficulty;
    private double accuracy;

    public static StatisticDto fromEntity(WordStatistic wordStatistic) {
        return StatisticDto.builder()
                .wordId(wordStatistic.getWordId())
                .correctCount(wordStatistic.getCorrectCount())
                .incorrectCount(wordStatistic.getIncorrectCount())
                .isLearned(wordStatistic.isLearned())
                .difficulty(wordStatistic.getDifficulty())
                .accuracy(wordStatistic.getAccuracy())
                .build();
    }
}

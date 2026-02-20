package com.kimtaeyang.mobidic.statistic.dto;

import com.kimtaeyang.mobidic.statistic.entity.WordStatistic;
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
    private int correctCount;
    private int incorrectCount;
    private boolean isLearned;
    private double difficulty;

    public static StatisticDto fromEntity(WordStatistic wordStatistic, double difficulty) {
        return StatisticDto.builder()
                .wordId(wordStatistic.getWordId())
                .correctCount(wordStatistic.getCorrectCount())
                .incorrectCount(wordStatistic.getIncorrectCount())
                .isLearned(wordStatistic.isLearned())
                .difficulty(difficulty)
                .build();
    }
}

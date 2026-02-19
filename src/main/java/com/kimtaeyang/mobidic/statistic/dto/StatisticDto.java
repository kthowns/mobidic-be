package com.kimtaeyang.mobidic.statistic.dto;

import com.kimtaeyang.mobidic.statistic.entity.Statistic;
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
    private int isLearned;
    private double difficulty;

    public static StatisticDto fromEntity(Statistic rate, double difficulty) {
        return StatisticDto.builder()
                .wordId(rate.getWordId())
                .correctCount(rate.getCorrectCount())
                .incorrectCount(rate.getIncorrectCount())
                .isLearned(rate.getIsLearned())
                .difficulty(difficulty)
                .build();
    }
}

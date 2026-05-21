package com.kthowns.mobidic.domain.statistic.implementation;

import com.kthowns.mobidic.domain.statistic.util.DifficultyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatisticCalculator {
    private final DifficultyUtil difficultyUtil;

    public double calculateDifficulty(long correctCount, long incorrectCount) {
        return difficultyUtil.calcDifficultyRatio(correctCount, incorrectCount);
    }

    public double calculateAccuracy(long correctCount, long incorrectCount) {
        return difficultyUtil.calcAccuracyRatio(correctCount, incorrectCount);
    }

    public double calculateAverageAccuracy(long correctCount, long incorrectCount) {
        if (incorrectCount == 0) {
            return correctCount > 0 ? 1.0 : 0.0;
        }
        return (double) correctCount / (correctCount + incorrectCount);
    }
}

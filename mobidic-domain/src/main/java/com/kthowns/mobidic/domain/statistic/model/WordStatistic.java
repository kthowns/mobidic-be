package com.kthowns.mobidic.domain.statistic.model;

import java.util.UUID;

public record WordStatistic(
        UUID wordId,
        long correctCount,
        long incorrectCount,
        boolean isLearned,
        double difficulty,
        double accuracy
) {
    public static WordStatistic create(UUID wordId) {
        return new WordStatistic(
                wordId,
                0,
                0,
                false,
                0.5,
                0.0
        );
    }

    public WordStatistic increaseCorrectCount() {
        long newCorrectCount = this.correctCount + 1;
        return new WordStatistic(
                this.wordId,
                newCorrectCount,
                this.incorrectCount,
                this.isLearned,
                calculateDifficulty(newCorrectCount, this.incorrectCount),
                calculateAccuracy(newCorrectCount, this.incorrectCount)
        );
    }

    public WordStatistic increaseIncorrectCount() {
        long newIncorrectCount = this.incorrectCount + 1;
        return new WordStatistic(
                this.wordId,
                this.correctCount,
                newIncorrectCount,
                this.isLearned,
                calculateDifficulty(this.correctCount, newIncorrectCount),
                calculateAccuracy(this.correctCount, newIncorrectCount)
        );
    }

    public WordStatistic toggleLearned() {
        return new WordStatistic(
                this.wordId,
                this.correctCount,
                this.incorrectCount,
                !this.isLearned,
                this.difficulty,
                this.accuracy
        );
    }

    public static double calculateAverageAccuracy(long correct, long incorrect) {
        if (incorrect == 0) {
            return correct > 0 ? 1.0 : 0.0;
        }
        return (double) correct / (correct + incorrect);
    }

    private double calculateAccuracy(long correct, long incorrect) {
        long total = correct + incorrect;
        if (total == 0) return 0.0;
        return (double) correct / total;
    }

    private double calculateDifficulty(long correct, long incorrect) {
        double diff = (-0.045 * correct) + (0.055 * incorrect) + 0.5;
        return Math.min(1.0, Math.max(0.0, diff));
    }
}

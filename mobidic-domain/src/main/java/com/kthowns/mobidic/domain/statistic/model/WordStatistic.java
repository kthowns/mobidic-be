package com.kthowns.mobidic.domain.statistic.model;

import lombok.*;

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

    public void setLearned(boolean isLearned) {
        this.isLearned = isLearned;
    }

    public void setCorrectCount(Long correctCount) {
        this.correctCount = correctCount;
    }

    public void setIncorrectCount(Long incorrectCount) {
        this.incorrectCount = incorrectCount;
    }

    public void setDifficulty(double difficulty) {
        this.difficulty = difficulty;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }
}


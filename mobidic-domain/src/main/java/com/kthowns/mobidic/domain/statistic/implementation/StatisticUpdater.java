package com.kthowns.mobidic.domain.statistic.implementation;

import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.domain.statistic.repository.WordStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StatisticUpdater {
    private final WordStatisticRepository wordStatisticRepository;
    private final StatisticCalculator statisticCalculator;
    private final StatisticReader statisticReader;

    public void update(UUID userId, UUID wordId, Long correctCount, Long incorrectCount, boolean isLearned) {
        WordStatistic wordStatistic = WordStatistic.builder()
                .wordId(wordId)
                .correctCount(correctCount)
                .incorrectCount(incorrectCount)
                .isLearned(isLearned)
                .difficulty(statisticCalculator.calculateDifficulty(correctCount, incorrectCount))
                .accuracy(statisticCalculator.calculateAccuracy(correctCount, incorrectCount))
                .build();
        
        wordStatisticRepository.update(wordStatistic);
    }
}

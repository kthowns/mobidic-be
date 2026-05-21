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
        WordStatistic wordStatistic = statisticReader.readForUpdate(wordId, userId);
        
        wordStatistic.setCorrectCount(correctCount);
        wordStatistic.setIncorrectCount(incorrectCount);
        wordStatistic.setLearned(isLearned);
        
        wordStatistic.setDifficulty(statisticCalculator.calculateDifficulty(correctCount, incorrectCount));
        wordStatistic.setAccuracy(statisticCalculator.calculateAccuracy(correctCount, incorrectCount));
        
        wordStatisticRepository.save(wordStatistic);
    }
}

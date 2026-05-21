package com.kthowns.mobidic.domain.statistic.implementation;

import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.domain.statistic.repository.WordStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatisticUpdater {
    private final WordStatisticRepository wordStatisticRepository;
    private final StatisticCalculator statisticCalculator;

    public void updateStatistic(WordStatistic wordStatistic) {
        wordStatistic.setDifficulty(statisticCalculator.calculateDifficulty(
                wordStatistic.getCorrectCount(), wordStatistic.getIncorrectCount()
        ));
        wordStatistic.setAccuracy(statisticCalculator.calculateAccuracy(
                wordStatistic.getCorrectCount(), wordStatistic.getIncorrectCount()
        ));
        wordStatisticRepository.save(wordStatistic);
    }
}

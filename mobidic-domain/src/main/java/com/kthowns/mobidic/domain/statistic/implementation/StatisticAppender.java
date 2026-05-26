package com.kthowns.mobidic.domain.statistic.implementation;

import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.domain.statistic.repository.WordStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StatisticAppender {
    private final WordStatisticRepository wordStatisticRepository;

    public void append(UUID wordId) {
        WordStatistic wordStatistic = WordStatistic.create(wordId);
        wordStatisticRepository.append(wordStatistic);
    }
}

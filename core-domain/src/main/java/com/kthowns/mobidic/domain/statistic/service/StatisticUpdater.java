package com.kthowns.mobidic.domain.statistic.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.domain.statistic.repository.WordStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class StatisticUpdater {
    private final WordStatisticRepository wordStatisticRepository;

    public void toggleLearned(UUID userId, UUID wordId) {
        WordStatistic wordStatistic = wordStatisticRepository.readForUpdate(wordId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_STAT));

        wordStatisticRepository.update(wordStatistic.toggleLearned(), userId);
    }

    public void increaseCorrectCount(UUID userId, UUID wordId) {
        WordStatistic wordStatistic = wordStatisticRepository.readForUpdate(wordId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_STAT));

        wordStatisticRepository.update(wordStatistic.increaseCorrectCount(), userId);
    }

    public void increaseIncorrectCount(UUID userId, UUID wordId) {
        WordStatistic wordStatistic = wordStatisticRepository.readForUpdate(wordId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_STAT));

        wordStatisticRepository.update(wordStatistic.increaseIncorrectCount(), userId);
    }
}

package com.kthowns.mobidic.domain.statistic.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.domain.statistic.repository.WordStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class StatisticReader {
    private final WordStatisticRepository wordStatisticRepository;

    public WordStatistic readByWordIdAndUserId(UUID wordId, UUID userId) {
        return wordStatisticRepository.readByWordIdAndUserId(wordId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_STAT));
    }

    public List<WordStatistic> readByVocabularyId(UUID vocabularyId) {
        return wordStatisticRepository.readByVocabularyId(vocabularyId);
    }

    public List<WordStatistic> readByUserId(UUID userId) {
        return wordStatisticRepository.readByUserId(userId);
    }

    public double readVocabLearningRate(UUID vocabularyId, UUID userId) {
        return wordStatisticRepository.calculateVocabularyLearningRate(vocabularyId, userId);
    }
}

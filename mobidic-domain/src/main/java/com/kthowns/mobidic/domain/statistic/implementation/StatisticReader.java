package com.kthowns.mobidic.domain.statistic.implementation;

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
public class StatisticReader {
    private final WordStatisticRepository wordStatisticRepository;

    public WordStatistic readByWordIdAndUserId(UUID wordId, UUID userId) {
        return wordStatisticRepository.readByWordIdAndUserId(wordId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_STAT));
    }

    public WordStatistic readForUpdate(UUID wordId, UUID userId) {
        return wordStatisticRepository.readForUpdate(wordId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_STAT));
    }

    public List<WordStatistic> readByVocabularyId(UUID vocabularyId) {
        return wordStatisticRepository.readByVocabularyId(vocabularyId);
    }

    public List<WordStatistic> readByUserId(UUID userId) {
        return wordStatisticRepository.readByUserId(userId);
    }

    public Double readVocabLearningRate(UUID vocabularyId, UUID userId) {
        return wordStatisticRepository.calculateVocabularyLearningRate(vocabularyId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.INTERNAL_SERVER_ERROR));
    }
}

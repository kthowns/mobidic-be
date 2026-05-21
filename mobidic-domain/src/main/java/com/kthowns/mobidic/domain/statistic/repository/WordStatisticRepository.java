package com.kthowns.mobidic.domain.statistic.repository;

import com.kthowns.mobidic.domain.statistic.model.WordStatistic;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WordStatisticRepository {
    void save(WordStatistic wordStatistic);

    Optional<WordStatistic> readByWordIdAndUserId(UUID wordId, UUID userId);

    Optional<WordStatistic> readForUpdate(UUID wordId, UUID userId);

    List<WordStatistic> readByVocabularyId(UUID vocabularyId);

    List<WordStatistic> readByUserId(UUID userId);

    Optional<Double> calculateVocabularyLearningRate(UUID vocabularyId, UUID userId);
}

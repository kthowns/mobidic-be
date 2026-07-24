package com.kthowns.mobidic.storage.statistic.repository.jpa;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.domain.statistic.repository.WordStatisticRepository;
import com.kthowns.mobidic.storage.statistic.jpaentity.WordStatisticJpaEntity;
import com.kthowns.mobidic.storage.statistic.jparepository.WordStatisticJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class WordStatisticRepositoryImpl implements WordStatisticRepository {
    private final WordStatisticJpaRepository wordStatisticJpaRepository;

    @Override
    public void append(WordStatistic wordStatistic) {
        WordStatisticJpaEntity wordStatisticJpaEntity = WordStatisticJpaEntity.createFromModel(wordStatistic);

        wordStatisticJpaRepository.save(wordStatisticJpaEntity);
    }

    @Override
    public void update(WordStatistic wordStatistic, UUID userId) {
        WordStatisticJpaEntity wordStatisticJpaEntity = wordStatisticJpaRepository.findByWordIdAndUserId(wordStatistic.wordId(), userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_STAT));

        wordStatisticJpaEntity.updateFromModel(wordStatistic);
    }

    @Override
    public Optional<WordStatistic> readByWordIdAndUserId(UUID wordId, UUID userId) {
        return wordStatisticJpaRepository.findByWordIdAndUserId(wordId, userId)
                .map(WordStatisticJpaEntity::toModel);
    }

    @Override
    public Optional<WordStatistic> readForUpdate(UUID wordId, UUID userId) {
        return wordStatisticJpaRepository.findForUpdate(wordId, userId)
                .map(WordStatisticJpaEntity::toModel);
    }

    @Override
    public List<WordStatistic> readByVocabularyId(UUID vocabularyId, UUID userId) {
        return wordStatisticJpaRepository.findByVocabularyIdAndUserId(vocabularyId, userId).stream()
                .map(WordStatisticJpaEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<WordStatistic> readByUserId(UUID userId) {
        return wordStatisticJpaRepository.findByUserId(userId).stream()
                .map(WordStatisticJpaEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public double calculateVocabularyLearningRate(UUID vocabularyId, UUID userId) {
        return wordStatisticJpaRepository.getVocabularyLearningRate(vocabularyId, userId);
    }
}

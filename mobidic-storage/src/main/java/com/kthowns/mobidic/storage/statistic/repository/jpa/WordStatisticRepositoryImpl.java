package com.kthowns.mobidic.storage.statistic.repository.jpa;

import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.domain.statistic.repository.WordStatisticRepository;
import com.kthowns.mobidic.storage.statistic.jpaentity.WordStatisticJpaEntity;
import com.kthowns.mobidic.storage.statistic.jparepository.WordStatisticJpaRepository;
import com.kthowns.mobidic.storage.word.jpaentity.WordJpaEntity;
import jakarta.persistence.EntityManager;
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
    private final EntityManager em;

    @Override
    public void append(WordStatistic wordStatistic) {
        WordJpaEntity wordRef =
                em.getReference(WordJpaEntity.class, wordStatistic.wordId());
        WordStatisticJpaEntity wordStatisticJpaEntity = WordStatisticJpaEntity.builder()
                .word(wordRef)
                .build();

        wordStatisticJpaRepository.save(wordStatisticJpaEntity);
    }

    @Override
    public void update(WordStatistic wordStatistic) {

    }

    @Override
    public Optional<WordStatistic> readByWordIdAndUserId(UUID wordId, UUID userId) {
        return wordStatisticJpaRepository.findByWordIdAndWord_Vocabulary_User_Id(wordId, userId)
                .map(WordStatisticJpaEntity::toModel);
    }

    @Override
    public Optional<WordStatistic> readForUpdate(UUID wordId, UUID userId) {
        return wordStatisticJpaRepository.findForUpdate(wordId, userId)
                .map(WordStatisticJpaEntity::toModel);
    }

    @Override
    public List<WordStatistic> readByVocabularyId(UUID vocabularyId) {
        return wordStatisticJpaRepository.findByWord_Vocabulary_Id(vocabularyId).stream()
                .map(WordStatisticJpaEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<WordStatistic> readByUserId(UUID userId) {
        return wordStatisticJpaRepository.findByWord_Vocabulary_User_Id(userId).stream()
                .map(WordStatisticJpaEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Double> calculateVocabularyLearningRate(UUID vocabularyId, UUID userId) {
        return wordStatisticJpaRepository.getVocabularyLearningRate(vocabularyId, userId);
    }
}

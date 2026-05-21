package com.kthowns.mobidic.storage.statistic.repository.jpa;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.domain.statistic.repository.WordStatisticRepository;
import com.kthowns.mobidic.storage.statistic.jpaentity.WordStatisticJpaEntity;
import com.kthowns.mobidic.storage.statistic.jparepository.WordStatisticJpaRepository;
import com.kthowns.mobidic.storage.word.jpaentity.WordJpaEntity;
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
    public void save(WordStatistic wordStatistic) {
        WordStatisticJpaEntity entity = wordStatisticJpaRepository.findById(wordStatistic.getWordId())
                .orElseGet(() -> WordStatisticJpaEntity.builder()
                        .word(WordJpaEntity.builder().id(wordStatistic.getWordId()).build())
                        .build());

        // 도메인 모델의 필드들을 엔티티에 반영 (increaseCount 등은 엔티티 메서드 활용 가능하지만 여기선 필드 세팅)
        // 현재 엔티티 구조상 Setter가 제한적이므로, 도메인 모델의 값을 직접 넣어주는 방식으로 구현
        updateEntityFromModel(entity, wordStatistic);
        wordStatisticJpaRepository.save(entity);
    }

    @Override
    public Optional<WordStatistic> readByWordIdAndUserId(UUID wordId, UUID userId) {
        return wordStatisticJpaRepository.findByWordIdAndWord_Vocabulary_User_Id(wordId, userId)
                .map(this::mapToModel);
    }

    @Override
    public Optional<WordStatistic> readForUpdate(UUID wordId, UUID userId) {
        return wordStatisticJpaRepository.findForUpdate(wordId, userId)
                .map(this::mapToModel);
    }

    @Override
    public List<WordStatistic> readByVocabularyId(UUID vocabularyId) {
        return wordStatisticJpaRepository.findByWord_Vocabulary_Id(vocabularyId).stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<WordStatistic> readByUserId(UUID userId) {
        return wordStatisticJpaRepository.findByWord_Vocabulary_User_Id(userId).stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Double> calculateVocabularyLearningRate(UUID vocabularyId, UUID userId) {
        return wordStatisticJpaRepository.getVocabularyLearningRate(vocabularyId, userId);
    }

    private WordStatistic mapToModel(WordStatisticJpaEntity entity) {
        return WordStatistic.builder()
                .wordId(entity.getWordId())
                .correctCount(entity.getCorrectCount())
                .incorrectCount(entity.getIncorrectCount())
                .isLearned(entity.isLearned())
                .difficulty(entity.getDifficulty())
                .accuracy(entity.getAccuracy())
                .build();
    }

    private void updateEntityFromModel(WordStatisticJpaEntity entity, WordStatistic model) {
        entity.update(
                model.getCorrectCount(),
                model.getIncorrectCount(),
                model.isLearned(),
                model.getDifficulty(),
                model.getAccuracy()
        );
    }
}

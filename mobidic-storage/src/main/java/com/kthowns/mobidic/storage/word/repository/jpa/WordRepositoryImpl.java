package com.kthowns.mobidic.storage.word.repository.jpa;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.word.model.Word;
import com.kthowns.mobidic.domain.word.model.WordDetail;
import com.kthowns.mobidic.domain.word.repository.WordRepository;
import com.kthowns.mobidic.storage.vocabulary.jpaentity.VocabularyJpaEntity;
import com.kthowns.mobidic.storage.vocabulary.jparepository.VocabularyJpaRepository;
import com.kthowns.mobidic.storage.word.jpaentity.WordJpaEntity;
import com.kthowns.mobidic.storage.word.jparepository.WordJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WordRepositoryImpl implements WordRepository {
    private final WordJpaRepository wordJpaRepository;
    private final VocabularyJpaRepository vocabularyJpaRepository;

    @Override
    public Word append(Word word) {
        VocabularyJpaEntity vocabulary = vocabularyJpaRepository.findById(word.vocabularyId())
                .orElseThrow(() -> new IllegalArgumentException("Vocabulary not found: " + word.vocabularyId()));

        WordJpaEntity wordJpaEntity = WordJpaEntity.builder()
                .expression(word.expression())
                .vocabulary(vocabulary)
                .build();
        return wordJpaRepository.save(wordJpaEntity).toModel();
    }

    @Override
    public Optional<Word> readByIdAndUserId(UUID wordId, UUID userId) {
        return wordJpaRepository.findByIdAndVocabulary_User_Id(wordId, userId)
                .map(WordJpaEntity::toModel);
    }

    @Override
    public List<WordDetail> readDetailsByVocabularyId(UUID userId, UUID vocabularyId, boolean onlyNotLearned) {
        return wordJpaRepository.findWordDetailsByVocabularyId(userId, vocabularyId, onlyNotLearned);
    }

    @Override
    public void update(Word word) {
        WordJpaEntity wordJpaEntity = wordJpaRepository.findById(word.id())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));
        
        wordJpaEntity.update(word.expression());
        wordJpaRepository.save(wordJpaEntity);
    }

    @Override
    public void delete(UUID wordId, UUID userId) {
        WordJpaEntity wordJpaEntity = wordJpaRepository.findByIdAndVocabulary_User_Id(wordId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));
        wordJpaRepository.delete(wordJpaEntity);
    }

    @Override
    public boolean existsByExpressionAndVocabularyId(String expression, UUID vocabularyId) {
        return wordJpaRepository.existsByExpressionAndVocabulary_Id(expression, vocabularyId);
    }

    @Override
    public boolean existsByExpressionAndVocabularyIdAndIdNot(String expression, UUID vocabularyId, UUID wordId) {
        return wordJpaRepository.existsByExpressionAndVocabulary_IdAndIdNot(expression, vocabularyId, wordId);
    }
}

package com.kthowns.mobidic.domain.word.repository;

import com.kthowns.mobidic.domain.word.model.Word;
import com.kthowns.mobidic.domain.word.model.WordDetail;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WordRepository {
    Word append(Word word);

    Optional<Word> readByIdAndUserId(UUID wordId, UUID userId);

    List<WordDetail> readDetailsByVocabularyId(UUID userId, UUID vocabularyId, boolean onlyNotLearned);

    void update(Word word, UUID userId);

    void delete(UUID wordId, UUID userId);

    boolean existsByExpressionAndVocabularyId(String expression, UUID vocabularyId, UUID userId);

    boolean existsByExpressionAndVocabularyIdAndIdNot(String expression, UUID vocabularyId, UUID wordId, UUID userId);
}

package com.kthowns.mobidic.domain.word.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.statistic.implementation.StatisticAppender;
import com.kthowns.mobidic.domain.vocabulary.implementation.VocabularyManager;
import com.kthowns.mobidic.domain.vocabulary.implementation.VocabularyReader;
import com.kthowns.mobidic.domain.word.implementation.*;
import com.kthowns.mobidic.domain.word.model.Word;
import com.kthowns.mobidic.domain.word.model.WordDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class WordService {
    private final WordReader wordReader;
    private final WordAppender wordAppender;
    private final WordUpdater wordUpdater;
    private final WordRemover wordRemover;
    private final WordValidator wordValidator;

    private final VocabularyReader vocabularyReader;
    private final VocabularyManager vocabularyManager;
    private final StatisticAppender statisticAppender;

    @Transactional
    public Word addWord(UUID userId, UUID vocabId, String expression) {
        validateVocabularyExist(vocabId, userId);

        wordValidator.validateExpressionDuplication(expression, vocabId);

        Word word = wordAppender.append(expression, vocabId);

        // WordStatistic 생성 로직
        statisticAppender.append(word.id(), false);

        // Vocabulary 단어 수 원자적 업데이트
        vocabularyManager.increaseWordCount(vocabId);

        return word;
    }

    @Transactional(readOnly = true)
    public Word getWordById(UUID userId, UUID wordId) {
        return wordReader.readByIdAndUserId(wordId, userId);
    }

    @Transactional(readOnly = true)
    public List<WordDetail> getWordDetailsByVocabularyId(UUID userId, UUID vocabularyId) {
        validateVocabularyExist(vocabularyId, userId);

        return wordReader.readDetailsByVocabularyId(userId, vocabularyId, false);
    }

    @Transactional(readOnly = true)
    public List<WordDetail> getWordDetailsNotLearnedByVocabularyId(UUID userId, UUID vocabularyId) {
        validateVocabularyExist(vocabularyId, userId);

        return wordReader.readDetailsByVocabularyId(userId, vocabularyId, true);
    }

    @Transactional
    public void updateWord(UUID userId, UUID wordId, String expression) {
        Word word = wordReader.readByIdAndUserId(wordId, userId);

        wordValidator.validateExpressionUpdateDuplication(expression, word.vocabularyId(), wordId);

        wordUpdater.update(wordId, expression);
    }

    @Transactional
    public void deleteWord(UUID userId, UUID wordId) {
        Word word = wordReader.readByIdAndUserId(wordId, userId);

        wordRemover.remove(wordId, userId);

        // Vocabulary 단어 수 원자적 업데이트
        vocabularyManager.decreaseWordCount(word.vocabularyId());
    }

    private void validateVocabularyExist(UUID vocabularyId, UUID userId) {
        if (!vocabularyReader.existsByIdAndUser(vocabularyId, userId)) {
            throw new ApiException(GeneralResponseCode.NO_VOCAB);
        }
    }
}

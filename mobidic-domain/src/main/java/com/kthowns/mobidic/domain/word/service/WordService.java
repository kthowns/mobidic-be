package com.kthowns.mobidic.domain.word.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.domain.statistic.repository.WordStatisticRepository;
import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.domain.vocabulary.repository.VocabularyRepository;
import com.kthowns.mobidic.domain.word.implementation.*;
import com.kthowns.mobidic.domain.word.model.Word;
import com.kthowns.mobidic.domain.word.model.WordDetail;
import com.kthowns.mobidic.domain.word.repository.WordRepository;
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

    private final WordRepository wordRepository;
    private final VocabularyRepository vocabularyRepository;
    private final WordStatisticRepository wordStatisticRepository;

    @Transactional
    public void addWord(UUID userId, UUID vocabId, String expression) {
        Vocabulary vocabulary = vocabularyRepository
                .findForUpdate(vocabId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));

        wordValidator.validateExpressionDuplication(expression, vocabId);

        Word word = Word.builder()
                .expression(expression)
                .vocabularyId(vocabId)
                .build();
        wordAppender.append(word);

        // WordStatistic 생성 로직
        wordStatisticRepository.save(WordStatistic.builder()
                .wordId(word.getId())
                .isLearned(false)
                .build());

        // Vocabulary 단어 수 업데이트
        vocabulary.addWordCount();
        vocabularyRepository.save(vocabulary);
    }

    @Transactional(readOnly = true)
    public Word getWordById(UUID userId, UUID wordId) {
        return wordRepository.readByIdAndUserId(wordId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));
    }

    @Transactional(readOnly = true)
    public List<WordDetail> getWordDetailsByVocabularyId(UUID userId, UUID vocabularyId) {
        if (!vocabularyRepository.existsByIdAndUser_Id(vocabularyId, userId)) {
            throw new ApiException(GeneralResponseCode.NO_VOCAB);
        }

        return wordReader.readDetailsByVocabularyId(userId, vocabularyId, false);
    }

    @Transactional(readOnly = true)
    public List<WordDetail> getWordDetailsNotLearnedByVocabularyId(UUID userId, UUID vocabularyId) {
        if (!vocabularyRepository.existsByIdAndUser_Id(vocabularyId, userId)) {
            throw new ApiException(GeneralResponseCode.NO_VOCAB);
        }

        return wordReader.readDetailsByVocabularyId(userId, vocabularyId, true);
    }

    @Transactional
    public void updateWord(UUID userId, UUID wordId, String expression) {
        Word word = wordRepository.readByIdAndUserId(wordId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));

        wordValidator.validateExpressionUpdateDuplication(expression, word.getVocabularyId(), wordId);

        Word updatedWord = Word.builder()
                .id(wordId)
                .vocabularyId(word.getVocabularyId())
                .expression(expression)
                .build();
        wordUpdater.update(updatedWord);
    }

    @Transactional
    public void deleteWord(UUID userId, UUID wordId) {
        Word word = wordRepository.readByIdAndUserId(wordId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));

        Vocabulary vocabulary = vocabularyRepository.findForUpdate(word.getVocabularyId(), userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));

        wordRemover.remove(wordId, userId);

        vocabulary.removeWordCount();
        vocabularyRepository.save(vocabulary);
    }
}

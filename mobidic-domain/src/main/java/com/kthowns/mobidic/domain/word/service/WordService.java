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

        // WordStatistic 생성 로직 (추후 Statistic 도메인 리팩토링 시 이동 검토)
        wordStatisticRepository.save(WordStatistic.builder()
                .wordId(word.getId())
                .isLearned(false)
                .build());

        // Vocabulary 단어 수 업데이트는 JPA 엔티티의 Dirty Checking에 의존하거나 
        // 도메인 모델을 통해 처리 후 Repository에 반영하는 방식 중 선택 필요
        // 현재는 기존 로직 유지를 위해 Repository 인터페이스 확장이 필요할 수 있음
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

        wordRemover.remove(wordId, userId);
    }
}

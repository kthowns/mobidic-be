package com.kthowns.mobidic.domain.word.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.word.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class WordValidator {
    private final WordRepository wordRepository;

    public void validateExpressionDuplication(String expression, UUID vocabularyId, UUID userId) {
        if (wordRepository.existsByExpressionAndVocabularyId(expression, vocabularyId, userId)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_WORD);
        }
    }

    public void validateExpressionUpdateDuplication(String expression, UUID vocabularyId, UUID wordId, UUID userId) {
        if (wordRepository.existsByExpressionAndVocabularyIdAndIdNot(expression, vocabularyId, wordId, userId)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_WORD);
        }
    }
}

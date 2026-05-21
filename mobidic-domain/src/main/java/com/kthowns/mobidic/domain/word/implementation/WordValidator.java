package com.kthowns.mobidic.domain.word.implementation;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.word.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WordValidator {
    private final WordRepository wordRepository;

    public void validateExpressionDuplication(String expression, UUID vocabularyId) {
        if (wordRepository.existsByExpressionAndVocabularyId(expression, vocabularyId)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_WORD);
        }
    }

    public void validateExpressionUpdateDuplication(String expression, UUID vocabularyId, UUID wordId) {
        if (wordRepository.existsByExpressionAndVocabularyIdAndIdNot(expression, vocabularyId, wordId)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_WORD);
        }
    }
}

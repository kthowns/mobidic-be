package com.kthowns.mobidic.domain.word.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.word.repository.WordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WordValidatorTest {

    @Mock
    private WordRepository wordRepository;

    @InjectMocks
    private WordValidator wordValidator;

    @Test
    @DisplayName("validateExpressionDuplication 테스트 - 중복 없음 (통과)")
    void validateExpressionDuplicationTest_Success() {
        // Given
        String expression = "apple";
        UUID vocabularyId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        given(wordRepository.existsByExpressionAndVocabularyId(expression, vocabularyId, userId)).willReturn(false);

        // When & Then
        assertThatCode(() -> wordValidator.validateExpressionDuplication(expression, vocabularyId, userId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateExpressionDuplication 테스트 - 중복 발생 (예외)")
    void validateExpressionDuplicationTest_Fail() {
        // Given
        String expression = "apple";
        UUID vocabularyId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        given(wordRepository.existsByExpressionAndVocabularyId(expression, vocabularyId, userId)).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> wordValidator.validateExpressionDuplication(expression, vocabularyId, userId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.DUPLICATED_WORD.getMessage());
    }

    @Test
    @DisplayName("validateExpressionUpdateDuplication 테스트 - 중복 없음 (통과)")
    void validateExpressionDuplicationForUpdateTest_Success() {
        // Given
        String expression = "banana";
        UUID vocabularyId = UUID.randomUUID();
        UUID wordId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        given(wordRepository.existsByExpressionAndVocabularyIdAndIdNot(expression, vocabularyId, wordId, userId)).willReturn(false);

        // When & Then
        assertThatCode(() -> wordValidator.validateExpressionDuplicationForUpdate(expression, vocabularyId, wordId, userId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateExpressionUpdateDuplication 테스트 - 중복 발생 (예외)")
    void validateExpressionDuplicationForUpdateTest_Fail() {
        // Given
        String expression = "banana";
        UUID vocabularyId = UUID.randomUUID();
        UUID wordId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        given(wordRepository.existsByExpressionAndVocabularyIdAndIdNot(expression, vocabularyId, wordId, userId)).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> wordValidator.validateExpressionDuplicationForUpdate(expression, vocabularyId, wordId, userId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.DUPLICATED_WORD.getMessage());
    }
}

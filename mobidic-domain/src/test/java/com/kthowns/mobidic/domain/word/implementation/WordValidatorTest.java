package com.kthowns.mobidic.domain.word.implementation;

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
    private WordValidator target;

    @Test
    @DisplayName("validateExpressionDuplication 테스트 - 중복 없음 (통과)")
    void validateExpressionDuplicationTest_Success() {
        // Given
        String expression = "apple";
        UUID vocabularyId = UUID.randomUUID();
        given(wordRepository.existsByExpressionAndVocabularyId(expression, vocabularyId)).willReturn(false);

        // When & Then
        assertThatCode(() -> target.validateExpressionDuplication(expression, vocabularyId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateExpressionDuplication 테스트 - 중복 발생 (예외)")
    void validateExpressionDuplicationTest_Fail() {
        // Given
        String expression = "apple";
        UUID vocabularyId = UUID.randomUUID();
        given(wordRepository.existsByExpressionAndVocabularyId(expression, vocabularyId)).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> target.validateExpressionDuplication(expression, vocabularyId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.DUPLICATED_WORD.getMessage());
    }

    @Test
    @DisplayName("validateExpressionUpdateDuplication 테스트 - 중복 없음 (통과)")
    void validateExpressionUpdateDuplicationTest_Success() {
        // Given
        String expression = "banana";
        UUID vocabularyId = UUID.randomUUID();
        UUID wordId = UUID.randomUUID();
        given(wordRepository.existsByExpressionAndVocabularyIdAndIdNot(expression, vocabularyId, wordId)).willReturn(false);

        // When & Then
        assertThatCode(() -> target.validateExpressionUpdateDuplication(expression, vocabularyId, wordId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateExpressionUpdateDuplication 테스트 - 중복 발생 (예외)")
    void validateExpressionUpdateDuplicationTest_Fail() {
        // Given
        String expression = "banana";
        UUID vocabularyId = UUID.randomUUID();
        UUID wordId = UUID.randomUUID();
        given(wordRepository.existsByExpressionAndVocabularyIdAndIdNot(expression, vocabularyId, wordId)).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> target.validateExpressionUpdateDuplication(expression, vocabularyId, wordId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.DUPLICATED_WORD.getMessage());
    }
}

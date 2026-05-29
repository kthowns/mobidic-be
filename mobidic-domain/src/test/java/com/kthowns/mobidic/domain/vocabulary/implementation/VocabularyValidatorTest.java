package com.kthowns.mobidic.domain.vocabulary.implementation;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.vocabulary.repository.VocabularyRepository;
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
class VocabularyValidatorTest {

    @Mock
    private VocabularyRepository vocabularyRepository;

    @InjectMocks
    private VocabularyValidator target;

    @Test
    @DisplayName("validateTitleAppendDuplication 테스트 - 중복 없음 (통과)")
    void validateTitleAppendDuplicationTest_Success() {
        // Given
        String title = "단어장 제목";
        UUID userId = UUID.randomUUID();
        given(vocabularyRepository.existsByTitleAndUserId(title, userId)).willReturn(false);

        // When & Then
        assertThatCode(() -> target.validateTitleAppendDuplication(title, userId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateTitleAppendDuplication 테스트 - 중복 발생 (예외)")
    void validateTitleAppendDuplicationTest_Fail() {
        // Given
        String title = "단어장 제목";
        UUID userId = UUID.randomUUID();
        given(vocabularyRepository.existsByTitleAndUserId(title, userId)).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> target.validateTitleAppendDuplication(title, userId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.DUPLICATED_TITLE.getMessage());
    }

    @Test
    @DisplayName("validateTitleUpdateDuplication 테스트 - 중복 없음 (통과)")
    void validateTitleUpdateDuplicationTest_Success() {
        // Given
        String title = "단어장 제목";
        UUID vocabId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        given(vocabularyRepository.existsByTitleAndIdNotAndUserId(title, vocabId, userId)).willReturn(false);

        // When & Then
        assertThatCode(() -> target.validateTitleUpdateDuplication(title, vocabId, userId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateTitleUpdateDuplication 테스트 - 중복 발생 (예외)")
    void validateTitleUpdateDuplicationTest_Fail() {
        // Given
        String title = "단어장 제목";
        UUID vocabId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        given(vocabularyRepository.existsByTitleAndIdNotAndUserId(title, vocabId, userId)).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> target.validateTitleUpdateDuplication(title, vocabId, userId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.DUPLICATED_TITLE.getMessage());
    }
}

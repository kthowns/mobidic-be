package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
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
class DefinitionValidatorTest {

    @Mock
    private DefinitionRepository definitionRepository;

    @InjectMocks
    private DefinitionValidator definitionValidator;

    @Test
    @DisplayName("validateMeaningDuplication 테스트 - 중복 없음 (통과)")
    void validateMeaningDuplicationTest_Success() {
        // Given
        UUID wordId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String meaning = "의미";
        given(definitionRepository.existsByMeaningAndWordId(meaning, wordId, userId)).willReturn(false);

        // When & Then
        assertThatCode(() -> definitionValidator.validateMeaningDuplication(meaning, wordId, userId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateMeaningDuplication 테스트 - 중복 발생 (예외)")
    void validateMeaningDuplicationTest_Fail() {
        // Given
        UUID wordId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String meaning = "의미";
        given(definitionRepository.existsByMeaningAndWordId(meaning, wordId, userId)).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> definitionValidator.validateMeaningDuplication(meaning, wordId, userId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(com.kthowns.mobidic.common.code.GeneralResponseCode.DUPLICATED_DEFINITION.getMessage());
    }

    @Test
    @DisplayName("validateMeaningUpdateDuplication 테스트 (ID 제외) - 중복 없음 (통과)")
    void validateMeaningUpdateDuplicationWithIdTest_Success() {
        // Given
        UUID wordId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String meaning = "의미";
        UUID definitionId = UUID.randomUUID();
        given(definitionRepository.existsByMeaningAndWordIdAndIdNot(meaning, wordId, definitionId, userId)).willReturn(false);

        // When & Then
        assertThatCode(() -> definitionValidator.validateMeaningUpdateDuplication(meaning, wordId, definitionId, userId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateMeaningUpdateDuplication 테스트 (ID 제외) - 중복 발생 (예외)")
    void validateMeaningUpdateDuplicationWithIdTest_Fail() {
        // Given
        UUID wordId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String meaning = "의미";
        UUID definitionId = UUID.randomUUID();
        given(definitionRepository.existsByMeaningAndWordIdAndIdNot(meaning, wordId, definitionId, userId)).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> definitionValidator.validateMeaningUpdateDuplication(meaning, wordId, definitionId, userId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(com.kthowns.mobidic.common.code.GeneralResponseCode.DUPLICATED_DEFINITION.getMessage());
    }
}


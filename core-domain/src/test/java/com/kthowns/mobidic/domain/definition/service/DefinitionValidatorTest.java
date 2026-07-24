package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import com.kthowns.mobidic.domain.global.model.AuditTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DefinitionValidatorTest {

    @Mock
    private DefinitionRepository definitionRepository;

    @InjectMocks
    private DefinitionValidator definitionValidator;

    @Test
    @DisplayName("validateMeaningsDuplicationForAppend 테스트 - 중복 없음 (통과)")
    void validateMeaningsDuplicationForAppendTest_Success() {
        // Given
        UUID wordId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        List<String> meanings = List.of("의미1", "의미2");
        given(definitionRepository.existsByMeaningsForAppend(meanings, wordId, userId)).willReturn(false);

        // When & Then
        assertThatCode(() -> definitionValidator.validateMeaningsDuplicationForAppend(meanings, wordId, userId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateMeaningsDuplicationForAppend 테스트 - 중복 발생 (예외)")
    void validateMeaningsDuplicationForAppendTest_Fail() {
        // Given
        UUID wordId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        List<String> meanings = List.of("중복의미");
        given(definitionRepository.existsByMeaningsForAppend(meanings, wordId, userId)).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> definitionValidator.validateMeaningsDuplicationForAppend(meanings, wordId, userId))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("validateMeaningsDuplicationForUpdate 테스트 - 중복 없음 (통과)")
    void validateMeaningsDuplicationForUpdateTest_Success() {
        // Given
        UUID wordId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        List<Definition> definitions = List.of(
                new Definition(UUID.randomUUID(), wordId, "의미1", PartOfSpeech.NOUN, AuditTime.create())
        );
        given(definitionRepository.existsByMeaningsForUpdate(anyList(), anyList(), eq(wordId), eq(userId))).willReturn(false);

        // When & Then
        assertThatCode(() -> definitionValidator.validateMeaningsDuplicationForUpdate(definitions, wordId, userId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateMeaningsDuplicationForUpdate 테스트 - 중복 발생 (예외)")
    void validateMeaningsDuplicationForUpdateTest_Fail() {
        // Given
        UUID wordId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        List<Definition> definitions = List.of(
                new Definition(UUID.randomUUID(), wordId, "중복의미", PartOfSpeech.NOUN, AuditTime.create())
        );
        given(definitionRepository.existsByMeaningsForUpdate(anyList(), anyList(), eq(wordId), eq(userId))).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> definitionValidator.validateMeaningsDuplicationForUpdate(definitions, wordId, userId))
                .isInstanceOf(ApiException.class);
    }
}

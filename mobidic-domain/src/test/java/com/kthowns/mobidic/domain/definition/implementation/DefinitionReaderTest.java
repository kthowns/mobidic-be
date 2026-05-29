package com.kthowns.mobidic.domain.definition.implementation;

import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DefinitionReaderTest {

    @Mock
    private DefinitionRepository definitionRepository;

    @InjectMocks
    private DefinitionReader target;

    @Test
    @DisplayName("readByWordId 테스트 - 단어 ID로 정의 목록 조회 성공")
    void readByWordIdTest() {
        // Given
        UUID wordId = UUID.randomUUID();
        List<Definition> expectedDefinitions = List.of(
                new Definition(UUID.randomUUID(), wordId, "의미1", PartOfSpeech.NOUN),
                new Definition(UUID.randomUUID(), wordId, "의미2", PartOfSpeech.VERB)
        );
        given(definitionRepository.readByWordId(wordId)).willReturn(expectedDefinitions);

        // When
        List<Definition> actualDefinitions = target.readByWordId(wordId);

        // Then
        assertThat(actualDefinitions).isEqualTo(expectedDefinitions);
    }

    @Test
    @DisplayName("readByIdAndUserId 테스트 - 정의 ID와 사용자 ID로 조회 성공")
    void readByIdAndUserIdTest_Success() {
        // Given
        UUID definitionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Definition expectedDefinition = new Definition(definitionId, UUID.randomUUID(), "의미", PartOfSpeech.NOUN);
        given(definitionRepository.readByIdAndUserId(definitionId, userId)).willReturn(Optional.of(expectedDefinition));

        // When
        Definition actualDefinition = target.readByIdAndUserId(definitionId, userId);

        // Then
        assertThat(actualDefinition).isEqualTo(expectedDefinition);
    }

    @Test
    @DisplayName("readByIdAndUserId 테스트 - 조회 실패 (예외 발생)")
    void readByIdAndUserIdTest_Fail() {
        // Given
        UUID definitionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        given(definitionRepository.readByIdAndUserId(definitionId, userId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> target.readByIdAndUserId(definitionId, userId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.NO_DEF.getMessage());
    }
}

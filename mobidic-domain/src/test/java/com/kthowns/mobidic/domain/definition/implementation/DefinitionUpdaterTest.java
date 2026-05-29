package com.kthowns.mobidic.domain.definition.implementation;

import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefinitionUpdaterTest {

    @Mock
    private DefinitionRepository definitionRepository;

    @Mock
    private DefinitionReader definitionReader;

    @InjectMocks
    private DefinitionUpdater target;

    @Test
    @DisplayName("update 테스트 - 정의 수정 성공")
    void updateTest() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID defId = UUID.randomUUID();
        String meaning = "새로운 의미";
        PartOfSpeech part = PartOfSpeech.VERB;

        Definition existingDefinition = new Definition(defId, UUID.randomUUID(), "기존 의미", PartOfSpeech.NOUN);
        given(definitionReader.readByIdAndUserId(defId, userId)).willReturn(existingDefinition);

        // When
        target.update(userId, defId, meaning, part);

        // Then
        verify(definitionRepository).update(any(Definition.class));
    }
}


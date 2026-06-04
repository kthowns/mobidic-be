package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefinitionUpdaterTest {

    @Mock
    private DefinitionRepository definitionRepository;

    @InjectMocks
    private DefinitionUpdater definitionUpdater;

    @Test
    @DisplayName("updateAll 테스트 - 정의 목록 수정 성공")
    void updateAllTest() {
        // Given
        UUID wordId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        List<Definition> definitions = List.of(
                new Definition(UUID.randomUUID(), wordId, "의미1", PartOfSpeech.NOUN),
                new Definition(UUID.randomUUID(), wordId, "의미2", PartOfSpeech.VERB)
        );

        // When
        definitionUpdater.updateAll(definitions, wordId, userId);

        // Then
        verify(definitionRepository).updateAll(definitions, wordId, userId);
    }
}

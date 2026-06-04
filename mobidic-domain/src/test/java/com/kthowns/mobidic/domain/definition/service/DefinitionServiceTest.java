package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.domain.definition.command.AddDefinitionCommand;
import com.kthowns.mobidic.domain.definition.command.UpdateDefinitionCommand;
import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.domain.definition.util.DefinitionMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefinitionServiceTest {

    @InjectMocks
    private DefinitionService definitionService;

    @Mock
    private DefinitionReader definitionReader;
    @Mock
    private DefinitionAppender definitionAppender;
    @Mock
    private DefinitionUpdater definitionUpdater;
    @Mock
    private DefinitionRemover definitionRemover;
    @Mock
    private DefinitionValidator definitionValidator;
    @Mock
    private DefinitionMapper definitionMapper;

    private final UUID userId = UUID.randomUUID();
    private final UUID wordId = UUID.randomUUID();

    @Test
    @DisplayName("addDefinitions 테스트 - 성공")
    void addDefinitionsSuccess() {
        // Given
        List<AddDefinitionCommand> commands = List.of(
                new AddDefinitionCommand("의미1", PartOfSpeech.NOUN)
        );

        // When
        definitionService.addDefinitions(userId, wordId, commands);

        // Then
        verify(definitionValidator).validateMeaningsDuplicationForAppend(anyList(), eq(wordId), eq(userId));
        verify(definitionAppender).appendAll(anyList());
    }

    @Test
    @DisplayName("updateDefinitions 테스트 - 성공")
    void updateDefinitionsSuccess() {
        // Given
        UUID defId = UUID.randomUUID();
        List<UpdateDefinitionCommand> commands = List.of(
                new UpdateDefinitionCommand(defId, wordId, "수정의미", PartOfSpeech.VERB)
        );
        List<Definition> existing = List.of(new Definition(defId, wordId, "기존의미", PartOfSpeech.NOUN));
        List<Definition> updated = List.of(new Definition(defId, wordId, "수정의미", PartOfSpeech.VERB));

        given(definitionReader.readByIdsAndWordIdAndUserId(anyList(), eq(wordId), eq(userId))).willReturn(existing);
        given(definitionMapper.mapToUpdated(existing, commands)).willReturn(updated);

        // When
        definitionService.updateDefinitions(userId, wordId, commands);

        // Then
        verify(definitionValidator).validateMeaningsDuplicationForUpdate(updated, wordId, userId);
        verify(definitionUpdater).updateAll(updated, wordId, userId);
    }

    @Test
    @DisplayName("deleteDefinitions 테스트 - 성공")
    void deleteDefinitionsSuccess() {
        // Given
        List<UUID> ids = List.of(UUID.randomUUID());

        // When
        definitionService.deleteDefinitions(ids, wordId, userId);

        // Then
        verify(definitionRemover).removeAll(ids, wordId, userId);
    }
}

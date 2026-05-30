package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.domain.definition.implementation.*;
import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.domain.word.implementation.WordReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private WordReader wordReader;

    private final UUID userId = UUID.randomUUID();
    private final UUID wordId = UUID.randomUUID();
    private final UUID defId = UUID.randomUUID();

    @Test
    @DisplayName("[DefService] Add def success")
    void addDefinitionSuccess() {
        // given
        String meaning = "testMeaning";
        PartOfSpeech part = PartOfSpeech.NOUN;

        // when
        definitionService.addDefinition(userId, wordId, meaning, part);

        // then
        verify(wordReader).readByIdAndUserId(wordId, userId);
        verify(definitionValidator).validateMeaningDuplication(meaning, wordId);
        verify(definitionAppender).append(wordId, meaning, part);
    }

    @Test
    @DisplayName("[DefService] Get defs by word id success")
    void getDefinitionsByWordIdSuccess() {
        // given
        List<Definition> definitions = List.of(
                new Definition(defId, wordId, "meaning", PartOfSpeech.NOUN)
        );
        given(definitionReader.readByWordId(wordId)).willReturn(definitions);

        // when
        List<Definition> result = definitionService.getDefinitionsByWordId(userId, wordId);

        // then
        verify(wordReader).readByIdAndUserId(wordId, userId);
        assertEquals(definitions, result);
    }

    @Test
    @DisplayName("[DefService] Update def success")
    void updateDefinitionSuccess() {
        // given
        String newMeaning = "newMeaning";
        PartOfSpeech newPart = PartOfSpeech.VERB;
        Definition existingDefinition = new Definition(defId, wordId, "oldMeaning", PartOfSpeech.NOUN);
        given(definitionReader.readByIdAndUserId(defId, userId)).willReturn(existingDefinition);

        // when
        definitionService.updateDefinition(userId, defId, newMeaning, newPart);

        // then
        verify(definitionValidator).validateMeaningUpdateDuplication(newMeaning, wordId, defId);
        verify(definitionUpdater).update(userId, defId, newMeaning, newPart);
    }

    @Test
    @DisplayName("[DefService] Delete def success")
    void deleteDefinitionSuccess() {
        // when
        definitionService.deleteDefinition(userId, defId);

        // then
        verify(definitionReader).readByIdAndUserId(defId, userId);
        verify(definitionRemover).remove(defId, userId);
    }
}

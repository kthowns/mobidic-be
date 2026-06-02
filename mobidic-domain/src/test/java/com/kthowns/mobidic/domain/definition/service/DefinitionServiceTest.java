package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.definition.command.AddDefinitionCommand;
import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.domain.word.service.WordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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
    private WordService wordService;

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
        verify(wordService).getWordById(userId, wordId);
        verify(definitionValidator).validateMeaningDuplication(meaning, wordId);
        verify(definitionAppender).append(wordId, meaning, part);
    }

    @Test
    @DisplayName("[DefService] Add def fail - User does not own word (Security check)")
    void addDefinitionSecurityFail() {
        // given
        String meaning = "meaning";
        PartOfSpeech part = PartOfSpeech.NOUN;
        given(wordService.getWordById(userId, wordId))
                .willThrow(new ApiException(GeneralResponseCode.NO_WORD));

        // when
        ApiException exception = assertThrows(ApiException.class, () ->
                definitionService.addDefinition(userId, wordId, meaning, part));

        // then
        assertEquals(GeneralResponseCode.NO_WORD, exception.getResponseCode());
        verify(definitionValidator, never()).validateMeaningDuplication(anyString(), any(UUID.class));
        verify(definitionAppender, never()).append(any(UUID.class), anyString(), any(PartOfSpeech.class));
    }

    @Test
    @DisplayName("[DefService] Add definitions (batch) success")
    void addDefinitionsBatchSuccess() {
        // given
        List<AddDefinitionCommand> commands = List.of(
                AddDefinitionCommand.of("meaning1", PartOfSpeech.NOUN),
                AddDefinitionCommand.of("meaning2", PartOfSpeech.VERB)
        );

        // when
        definitionService.addDefinitions(userId, wordId, commands);

        // then
        verify(definitionValidator, times(2)).validateMeaningDuplication(anyString(), eq(wordId));
        verify(definitionAppender).appendAll(anyList());
    }

    @Test
    @DisplayName("[DefService] Add definitions (batch) success - empty list")
    void addDefinitionsBatchEmptySuccess() {
        // when
        definitionService.addDefinitions(userId, wordId, Collections.emptyList());

        // then
        verify(definitionValidator, never()).validateMeaningDuplication(anyString(), any(UUID.class));
        verify(definitionAppender, never()).appendAll(anyList());
    }

    @Test
    @DisplayName("[DefService] Add definitions (batch) fail - duplication error")
    void addDefinitionsBatchDuplicationFail() {
        // given
        List<AddDefinitionCommand> commands = List.of(
                AddDefinitionCommand.of("duplicate", PartOfSpeech.NOUN)
        );
        doThrow(new ApiException(GeneralResponseCode.DUPLICATED_DEFINITION))
                .when(definitionValidator).validateMeaningDuplication("duplicate", wordId);

        // when & then
        ApiException exception = assertThrows(ApiException.class, () ->
                definitionService.addDefinitions(userId, wordId, commands));

        assertEquals(GeneralResponseCode.DUPLICATED_DEFINITION, exception.getResponseCode());
        verify(definitionAppender, never()).appendAll(anyList());
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
        verify(wordService).getWordById(userId, wordId);
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

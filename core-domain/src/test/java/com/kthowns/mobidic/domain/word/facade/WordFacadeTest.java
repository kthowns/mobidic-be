package com.kthowns.mobidic.domain.word.facade;

import com.kthowns.mobidic.domain.definition.command.AddDefinitionCommand;
import com.kthowns.mobidic.domain.definition.command.UpdateDefinitionCommand;
import com.kthowns.mobidic.domain.definition.service.DefinitionService;
import com.kthowns.mobidic.domain.global.model.AuditTime;
import com.kthowns.mobidic.domain.statistic.service.StatisticService;
import com.kthowns.mobidic.domain.vocabulary.service.VocabularyService;
import com.kthowns.mobidic.domain.word.command.AddWordCommand;
import com.kthowns.mobidic.domain.word.command.UpdateWordCommand;
import com.kthowns.mobidic.domain.word.model.Word;
import com.kthowns.mobidic.domain.word.service.WordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class WordFacadeTest {

    @Mock
    private WordService wordService;
    @Mock
    private DefinitionService definitionService;
    @Mock
    private StatisticService statisticService;
    @Mock
    private VocabularyService vocabularyService;

    @InjectMocks
    private WordFacade wordFacade;

    private final UUID userId = UUID.randomUUID();
    private final UUID vocabId = UUID.randomUUID();
    private final UUID wordId = UUID.randomUUID();

    @Test
    @DisplayName("addWord 테스트 - 전체 프로세스 순서 및 호출 검증")
    void addWordTest() {
        // Given
        AddWordCommand addWordCommand = new AddWordCommand("apple");
        List<AddDefinitionCommand> addDefinitionCommands = List.of();
        Word word = new Word(wordId, vocabId, "apple", AuditTime.create());

        given(wordService.addWord(userId, vocabId, "apple")).willReturn(word);

        // When
        wordFacade.addWord(userId, vocabId, addWordCommand, addDefinitionCommands);

        // Then
        InOrder inOrder = inOrder(wordService, definitionService, statisticService, vocabularyService);
        inOrder.verify(wordService).addWord(userId, vocabId, "apple");
        inOrder.verify(definitionService).addDefinitions(userId, wordId, addDefinitionCommands);
        inOrder.verify(statisticService).append(wordId);
        inOrder.verify(vocabularyService).increaseWordCount(vocabId, userId);
    }

    @Test
    @DisplayName("updateWordAndSyncDefinitions 테스트 - 실행 순서(검증-수정-삭제-추가-수정) 검증")
    void updateWordAndSyncDefinitionsTest() {
        // Given
        UpdateWordCommand updateWordCommand = new UpdateWordCommand(wordId, "banana");
        List<UpdateDefinitionCommand> updateDefinitionCommands = List.of();
        List<AddDefinitionCommand> addDefinitionCommands = List.of();
        List<UUID> deletingDefinitionIds = List.of(UUID.randomUUID());

        // When
        wordFacade.updateWordAndSyncDefinitions(userId, updateWordCommand, updateDefinitionCommands, addDefinitionCommands, deletingDefinitionIds);

        // Then
        InOrder inOrder = inOrder(wordService, definitionService);
        inOrder.verify(definitionService).validateDefinitionRequests(anyList());
        inOrder.verify(definitionService).validateDefinitionIdsDuplication(anyList());
        inOrder.verify(wordService).updateWord(userId, wordId, "banana");
        inOrder.verify(definitionService).deleteDefinitions(deletingDefinitionIds, wordId, userId);
        inOrder.verify(definitionService).addDefinitions(userId, wordId, addDefinitionCommands);
        inOrder.verify(definitionService).updateDefinitions(userId, wordId, updateDefinitionCommands);
    }
}

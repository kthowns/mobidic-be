package com.kthowns.mobidic.domain.word.facade;

import com.kthowns.mobidic.domain.definition.command.AddDefinitionCommand;
import com.kthowns.mobidic.domain.definition.command.UpdateDefinitionCommand;
import com.kthowns.mobidic.domain.definition.service.DefinitionService;
import com.kthowns.mobidic.domain.statistic.service.StatisticService;
import com.kthowns.mobidic.domain.vocabulary.service.VocabularyService;
import com.kthowns.mobidic.domain.word.command.AddWordCommand;
import com.kthowns.mobidic.domain.word.command.UpdateWordCommand;
import com.kthowns.mobidic.domain.word.model.Word;
import com.kthowns.mobidic.domain.word.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class WordFacade {
    private final WordService wordService;
    private final DefinitionService definitionService;
    private final StatisticService statisticService;
    private final VocabularyService vocabularyService;

    @Transactional
    public void addWord(
            UUID userId,
            UUID vocabularyId,
            AddWordCommand addWordCommand,
            List<AddDefinitionCommand> addDefinitionCommands
    ) {
        Word word = wordService.addWord(
                userId,
                vocabularyId,
                addWordCommand.expression()
        );

        // Definition batch 생성
        definitionService.addDefinitions(userId, word.id(), addDefinitionCommands);

        // WordStatistic 생성 로직
        statisticService.append(word.id());

        // Vocabulary 단어 수 원자적 업데이트
        vocabularyService.increaseWordCount(vocabularyId, userId);
    }

    @Transactional
    public void updateWordAndSyncDefinitions(
            UUID userId,
            UpdateWordCommand updateWordCommand,
            List<UpdateDefinitionCommand> updateDefinitionCommands,
            List<AddDefinitionCommand> addDefinitionCommands,
            List<UUID> deletingDefinitionIds
    ) {
        // Definitions 목록 검증
        definitionService.validateDefinitionRequests(
                Stream.concat(
                        updateDefinitionCommands.stream().map(UpdateDefinitionCommand::meaning),
                        addDefinitionCommands.stream().map(AddDefinitionCommand::meaning)
                ).toList()
        );
        definitionService.validateDefinitionIdsDuplication(
                Stream.concat(
                        updateDefinitionCommands.stream().map(UpdateDefinitionCommand::id),
                        deletingDefinitionIds.stream()
                ).toList()
        );

        // Word 업데이트
        wordService.updateWord(userId, updateWordCommand.wordId(), updateWordCommand.expression());

        // 삭제되는 Definitions 삭제
        definitionService.deleteDefinitions(deletingDefinitionIds, updateWordCommand.wordId(), userId);

        // 새로 추가되는 Definitions 추가
        definitionService.addDefinitions(userId, updateWordCommand.wordId(), addDefinitionCommands);

        // Definitions batch 업데이트
        definitionService.updateDefinitions(userId, updateWordCommand.wordId(), updateDefinitionCommands);
    }
}

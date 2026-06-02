package com.kthowns.mobidic.domain.word.facade;

import com.kthowns.mobidic.domain.definition.command.AddDefinitionCommand;
import com.kthowns.mobidic.domain.definition.service.DefinitionService;
import com.kthowns.mobidic.domain.statistic.service.StatisticService;
import com.kthowns.mobidic.domain.vocabulary.service.VocabularyService;
import com.kthowns.mobidic.domain.word.command.AddWordCommand;
import com.kthowns.mobidic.domain.word.model.Word;
import com.kthowns.mobidic.domain.word.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WordFacade {
    private final WordService wordService;
    private final DefinitionService definitionService;
    private final StatisticService statisticService;
    private final VocabularyService vocabularyService;

    @Transactional
    public void addWord(AddWordCommand addWordCommand, List<AddDefinitionCommand> addDefinitionCommands) {
        Word word = wordService.addWord(
                addWordCommand.userId(),
                addWordCommand.vocabId(),
                addWordCommand.expression()
        );

        // Definition batch 생성
        definitionService.addDefinitions(addWordCommand.userId(), word.id(), addDefinitionCommands);

        // WordStatistic 생성 로직
        statisticService.append(word.id());

        // Vocabulary 단어 수 원자적 업데이트
        vocabularyService.increaseWordCount(addWordCommand.vocabId());
    }

}

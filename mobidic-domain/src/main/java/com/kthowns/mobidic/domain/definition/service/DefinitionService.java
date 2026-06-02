package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.definition.command.AddDefinitionCommand;
import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.domain.word.service.WordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefinitionService {
    private final DefinitionReader definitionReader;
    private final DefinitionAppender definitionAppender;
    private final DefinitionUpdater definitionUpdater;
    private final DefinitionRemover definitionRemover;
    private final DefinitionValidator definitionValidator;

    private final WordService wordService;

    @Transactional
    public void addDefinition(
            UUID userId,
            UUID wordId,
            String meaning,
            PartOfSpeech part
    ) {
        wordService.getWordById(userId, wordId);

        definitionValidator.validateMeaningDuplication(meaning, wordId);

        definitionAppender.append(wordId, meaning, part);
    }

    @Transactional(readOnly = true)
    public List<Definition> getDefinitionsByWordId(UUID userId, UUID wordId) {
        wordService.getWordById(userId, wordId);

        return definitionReader.readByWordId(wordId);
    }

    @Transactional
    public void updateDefinition(
            UUID userId,
            UUID defId,
            String meaning,
            PartOfSpeech part
    ) {
        Definition definition = definitionReader.readByIdAndUserId(defId, userId);

        definitionValidator.validateMeaningUpdateDuplication(meaning, definition.wordId(), defId);

        definitionUpdater.update(userId, defId, meaning, part);
    }

    @Transactional
    public void deleteDefinition(
            UUID userId,
            UUID defId
    ) {
        definitionReader.readByIdAndUserId(defId, userId);

        definitionRemover.remove(defId, userId);
    }

    @Transactional
    public void addDefinitions(UUID userId, UUID wordId, List<AddDefinitionCommand> commands) {
        if (commands == null || commands.isEmpty()) {
            return;
        }

        // 중복 정의 체크 (요청 리스트 내 중복)
        long distinctCount = commands.stream()
                .map(AddDefinitionCommand::meaning)
                .distinct()
                .count();
        if (distinctCount != commands.size()) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_DEFINITION);
        }

        List<Definition> definitions = commands.stream()
                .peek(command -> definitionValidator.validateMeaningDuplication(command.meaning(), wordId))
                .map(command -> Definition.create(wordId, command.meaning(), command.part()))
                .toList();

        definitionAppender.appendAll(definitions);
    }
}

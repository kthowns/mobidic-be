package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.definition.command.AddDefinitionCommand;
import com.kthowns.mobidic.domain.definition.command.UpdateDefinitionCommand;
import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.util.DefinitionMapper;
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
    private final DefinitionMapper definitionMapper;

    private final WordService wordService;

    @Transactional(readOnly = true)
    public List<Definition> getDefinitionsByWordId(UUID userId, UUID wordId) {
        wordService.getWordById(userId, wordId);

        return definitionReader.readByWordId(wordId, userId);
    }

    @Transactional
    public void addDefinitions(UUID userId, UUID wordId, List<AddDefinitionCommand> commands) {
        if (commands == null || commands.isEmpty()) {
            return;
        }

        definitionValidator.validateMeaningsDuplicationForAppend(
                commands.stream().map(AddDefinitionCommand::meaning).toList(),
                wordId,
                userId
        );

        List<Definition> definitions = commands.stream()
                .map(command -> Definition.create(wordId, command.meaning(), command.part()))
                .toList();

        definitionAppender.appendAll(definitions);
    }

    @Transactional
    public void updateDefinitions(UUID userId, UUID wordId, List<UpdateDefinitionCommand> commands) {
        if (commands == null || commands.isEmpty()) {
            return;
        }

        // 존재하는 Definition Batch 조회
        List<Definition> existing = definitionReader.readByIdsAndWordIdAndUserId(
                commands.stream().map(UpdateDefinitionCommand::id).toList(),
                wordId,
                userId
        );

        // Model 객체 상태 변경 (Update)
        List<Definition> updated = definitionMapper.mapToUpdated(existing, commands);

        // 업데이트 된 Definitions 중복 체크
        definitionValidator.validateMeaningsDuplicationForUpdate(
                updated,
                wordId,
                userId
        );

        // Definition 영속화
        definitionUpdater.updateAll(updated, wordId, userId);
    }

    @Transactional
    public void deleteDefinitions(List<UUID> ids, UUID wordId, UUID userId) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        definitionRemover.removeAll(ids, wordId, userId);
    }

    public void validateDefinitionRequests(List<String> meanings) {
        long distinctCount = meanings
                .stream()
                .distinct()
                .count();
        if (distinctCount != meanings.size()) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_DEFINITION);
        }
    }

    public void validateDefinitionIdsDuplication(List<UUID> allIds) {
        long distinctCount = allIds
                .stream()
                .distinct()
                .count();
        if (distinctCount != allIds.size()) {
            throw new ApiException(GeneralResponseCode.INVALID_REQUEST_BODY);
        }
    }
}

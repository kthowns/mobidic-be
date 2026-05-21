package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.definition.implementation.*;
import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import com.kthowns.mobidic.domain.word.repository.WordRepository;
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

    private final WordRepository wordRepository;
    private final DefinitionRepository definitionRepository;

    @Transactional
    public void addDefinition(
            UUID userId,
            UUID wordId,
            String meaning,
            PartOfSpeech part
    ) {
        wordRepository.readByIdAndUserId(wordId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));

        definitionValidator.validateMeaningDuplication(meaning, wordId);

        Definition definition = Definition.builder()
                .wordId(wordId)
                .part(part)
                .meaning(meaning)
                .build();
        definitionAppender.append(definition);
    }

    @Transactional(readOnly = true)
    public List<Definition> getDefinitionsByWordId(UUID userId, UUID wordId) {
        wordRepository.readByIdAndUserId(wordId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));

        return definitionReader.readByWordId(wordId);
    }

    @Transactional
    public void updateDefinition(
            UUID userId,
            UUID defId,
            String meaning,
            PartOfSpeech part
    ) {
        Definition definition = definitionRepository.readByIdAndUserId(defId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_DEF));

        definitionValidator.validateMeaningUpdateDuplication(meaning, definition.getWordId(), defId);

        Definition updatedDefinition = Definition.builder()
                .id(defId)
                .wordId(definition.getWordId())
                .meaning(meaning)
                .part(part)
                .build();
        definitionUpdater.update(updatedDefinition);
    }

    @Transactional
    public void deleteDefinition(
            UUID userId,
            UUID defId
    ) {
        definitionRepository.readByIdAndUserId(defId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_DEF));

        definitionRemover.remove(defId, userId);
    }
}

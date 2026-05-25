package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.definition.implementation.*;
import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.domain.word.implementation.WordReader;
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

    private final WordReader wordReader;

    @Transactional
    public void addDefinition(
            UUID userId,
            UUID wordId,
            String meaning,
            PartOfSpeech part
    ) {
        wordReader.readByIdAndUserId(wordId, userId);

        definitionValidator.validateMeaningDuplication(meaning, wordId);

        definitionAppender.append(wordId, meaning, part);
    }

    @Transactional(readOnly = true)
    public List<Definition> getDefinitionsByWordId(UUID userId, UUID wordId) {
        wordReader.readByIdAndUserId(wordId, userId);

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

        definitionUpdater.update(defId, meaning, part);
    }

    @Transactional
    public void deleteDefinition(
            UUID userId,
            UUID defId
    ) {
        definitionReader.readByIdAndUserId(defId, userId);

        definitionRemover.remove(defId, userId);
    }
}

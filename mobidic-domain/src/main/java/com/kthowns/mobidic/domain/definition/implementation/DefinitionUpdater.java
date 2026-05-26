package com.kthowns.mobidic.domain.definition.implementation;

import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DefinitionUpdater {
    private final DefinitionRepository definitionRepository;
    private final DefinitionReader definitionReader;

    public void update(UUID userId, UUID defId, String meaning, PartOfSpeech part) {
        Definition definition = definitionReader.readByIdAndUserId(defId, userId);
        definitionRepository.update(definition.update(meaning, part));
    }
}

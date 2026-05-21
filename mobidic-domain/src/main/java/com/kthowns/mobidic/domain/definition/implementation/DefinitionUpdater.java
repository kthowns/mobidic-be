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

    public void update(UUID defId, String meaning, PartOfSpeech part) {
        Definition definition = Definition.builder()
                .id(defId)
                .meaning(meaning)
                .part(part)
                .build();
        definitionRepository.update(definition);
    }
}

package com.kthowns.mobidic.domain.definition.implementation;

import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefinitionUpdater {
    private final DefinitionRepository definitionRepository;

    public void update(Definition definition) {
        definitionRepository.update(definition);
    }
}

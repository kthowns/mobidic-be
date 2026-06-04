package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class DefinitionUpdater {
    private final DefinitionRepository definitionRepository;

    public void updateAll(List<Definition> definitions, UUID wordId, UUID userId) {
        definitionRepository.updateAll(definitions, wordId, userId);
    }
}

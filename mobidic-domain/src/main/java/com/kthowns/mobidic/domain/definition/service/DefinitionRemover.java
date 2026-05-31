package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class DefinitionRemover {
    private final DefinitionRepository definitionRepository;

    public void remove(UUID definitionId, UUID userId) {
        definitionRepository.delete(definitionId, userId);
    }
}

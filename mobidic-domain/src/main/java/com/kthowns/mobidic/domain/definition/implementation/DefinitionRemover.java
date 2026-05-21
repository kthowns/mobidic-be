package com.kthowns.mobidic.domain.definition.implementation;

import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DefinitionRemover {
    private final DefinitionRepository definitionRepository;

    public void remove(UUID definitionId, UUID userId) {
        definitionRepository.delete(definitionId, userId);
    }
}

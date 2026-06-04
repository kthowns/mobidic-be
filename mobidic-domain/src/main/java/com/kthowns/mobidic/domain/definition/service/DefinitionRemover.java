package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class DefinitionRemover {
    private final DefinitionRepository definitionRepository;

    public void removeAll(List<UUID> ids, UUID wordId, UUID userId) {
        definitionRepository.deleteAll(ids, wordId, userId);
    }
}

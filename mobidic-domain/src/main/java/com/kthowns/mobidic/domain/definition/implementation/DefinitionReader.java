package com.kthowns.mobidic.domain.definition.implementation;

import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DefinitionReader {
    private final DefinitionRepository definitionRepository;

    public List<Definition> readByWordId(UUID wordId) {
        return definitionRepository.readByWordId(wordId);
    }
}

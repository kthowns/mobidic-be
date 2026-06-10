package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class DefinitionAppender {
    private final DefinitionRepository definitionRepository;

    public void appendAll(List<Definition> definitions) {
        definitionRepository.appendAll(definitions);
    }
}

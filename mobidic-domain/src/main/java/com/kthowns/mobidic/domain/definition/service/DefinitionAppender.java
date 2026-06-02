package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class DefinitionAppender {
    private final DefinitionRepository definitionRepository;

    public void append(UUID wordId, String meaning, PartOfSpeech part) {
        definitionRepository.append(Definition.create(wordId, meaning, part));
    }

    public void appendAll(List<Definition> definitions) {
        definitionRepository.appendAll(definitions);
    }
}

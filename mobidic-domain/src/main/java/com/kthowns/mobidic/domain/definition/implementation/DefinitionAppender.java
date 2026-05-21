package com.kthowns.mobidic.domain.definition.implementation;

import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DefinitionAppender {
    private final DefinitionRepository definitionRepository;

    public void append(UUID wordId, String meaning, PartOfSpeech part) {
        Definition definition = Definition.builder()
                .wordId(wordId)
                .meaning(meaning)
                .part(part)
                .build();
        definitionRepository.append(definition);
    }
}

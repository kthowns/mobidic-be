package com.kimtaeyang.mobidic.security.accesshandler;

import com.kimtaeyang.mobidic.dictionary.entity.Definition;
import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
import com.kimtaeyang.mobidic.dictionary.entity.Word;
import com.kimtaeyang.mobidic.dictionary.repository.DefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefinitionAccessHandler extends AccessHandler {
    private final DefinitionRepository definitionRepository;

    @Override
    boolean isResourceOwner(UUID resourceId) {
        return definitionRepository.findById(resourceId)
                .map(Definition::getWord)
                .map(Word::getVocabulary)
                .map(Vocabulary::getUser)
                .filter((m) -> getCurrentUserId().equals(m.getId()))
                .isPresent();
    }
}

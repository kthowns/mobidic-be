package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class DefinitionReader {
    private final DefinitionRepository definitionRepository;

    public List<Definition> readByWordId(UUID wordId) {
        return definitionRepository.readByWordId(wordId);
    }

    public Definition readByIdAndUserId(UUID definitionId, UUID userId) {
        return definitionRepository.readByIdAndUserId(definitionId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_DEF));
    }
}

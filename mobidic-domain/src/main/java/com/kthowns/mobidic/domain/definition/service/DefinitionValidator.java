package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class DefinitionValidator {
    private final DefinitionRepository definitionRepository;

    public void validateMeaningDuplication(String meaning, UUID wordId, UUID userId) {
        if (definitionRepository.existsByMeaningAndWordId(meaning, wordId, userId)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_DEFINITION);
        }
    }

    public void validateMeaningUpdateDuplication(String meaning, UUID wordId, UUID definitionId, UUID userId) {
        if (definitionRepository.existsByMeaningAndWordIdAndIdNot(meaning, wordId, definitionId, userId)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_DEFINITION);
        }
    }
}

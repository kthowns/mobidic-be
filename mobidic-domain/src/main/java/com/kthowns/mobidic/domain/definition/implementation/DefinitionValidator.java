package com.kthowns.mobidic.domain.definition.implementation;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DefinitionValidator {
    private final DefinitionRepository definitionRepository;

    public void validateMeaningDuplication(String meaning, UUID wordId) {
        if (definitionRepository.existsByMeaningAndWordId(meaning, wordId)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_DEFINITION);
        }
    }

    public void validateMeaningUpdateDuplication(String meaning, UUID wordId, UUID definitionId) {
        if (definitionRepository.existsByMeaningAndWordIdAndIdNot(meaning, wordId, definitionId)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_DEFINITION);
        }
    }
}

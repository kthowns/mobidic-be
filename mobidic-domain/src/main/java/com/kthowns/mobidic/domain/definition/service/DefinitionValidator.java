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
class DefinitionValidator {
    private final DefinitionRepository definitionRepository;

    public void validateMeaningsDuplicationForAppend(List<String> meanings, UUID wordId, UUID userId) {
        // 새로운 뜻에 대한 중복 체크
        if (definitionRepository.existsByMeaningsForAppend(meanings, wordId, userId)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_DEFINITION);
        }
    }

    public void validateMeaningsDuplicationForUpdate(List<Definition> definitions, UUID wordId, UUID userId) {
        List<String> meanings = definitions.stream().map(Definition::meaning).toList();
        List<UUID> ids = definitions.stream().map(Definition::id).toList();

        // 기존 뜻에 대한 중복 체크 (변경사항이 없는 경우 중복이 아님으로 간주)
        if (definitionRepository.existsByMeaningsForUpdate(meanings, ids, wordId, userId)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_DEFINITION);
        }
    }
}

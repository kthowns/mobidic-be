package com.kthowns.mobidic.domain.vocabulary.implementation;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.vocabulary.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VocabularyValidator {
    private final VocabularyRepository vocabularyRepository;

    public void validateTitleAppendDuplication(String title, UUID userId) {
        if (vocabularyRepository.existsByTitleAndUserId(title, userId)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_TITLE);
        }
    }

    public void validateTitleUpdateDuplication(String title, UUID vocabularyId, UUID userId) {
        if (vocabularyRepository.existsByTitleAndIdNotAndUserId(title, vocabularyId, userId)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_TITLE);
        }
    }
}

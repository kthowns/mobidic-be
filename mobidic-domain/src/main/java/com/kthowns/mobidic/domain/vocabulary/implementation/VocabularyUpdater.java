package com.kthowns.mobidic.domain.vocabulary.implementation;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VocabularyUpdater {
    public void update(String title, String description, UUID vocabularyId, UUID userId) {
        Vocabulary vocabulary = vocabularyUpdater.
                vocabularyRepository.findForUpdate(vocabularyId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));
    }
}

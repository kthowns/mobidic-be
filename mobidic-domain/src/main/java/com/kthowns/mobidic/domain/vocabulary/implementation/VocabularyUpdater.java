package com.kthowns.mobidic.domain.vocabulary.implementation;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.domain.vocabulary.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VocabularyUpdater {
    private final VocabularyRepository vocabularyRepository;

    public void update(String title, String description, UUID vocabularyId, UUID userId) {
        // Patch vs Put 방식 결정 필요
        vocabularyRepository.update(title, description, vocabularyId, userId);
    }
}

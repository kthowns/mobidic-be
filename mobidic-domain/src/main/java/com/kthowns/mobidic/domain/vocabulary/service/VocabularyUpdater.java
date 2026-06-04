package com.kthowns.mobidic.domain.vocabulary.service;

import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.domain.vocabulary.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class VocabularyUpdater {
    private final VocabularyRepository vocabularyRepository;
    private final VocabularyReader vocabularyReader;

    public void update(UUID userId, UUID vocabularyId, String title, String description) {
        Vocabulary vocabulary = vocabularyReader.readById(vocabularyId, userId);

        vocabularyRepository.update(vocabulary.updateInfo(title, description));
    }

    public void increaseWordCount(UUID vocabularyId, UUID userId) {
        vocabularyRepository.increaseWordCount(vocabularyId, userId);
    }

    public void decreaseWordCount(UUID vocabularyId, UUID userId) {
        vocabularyRepository.decreaseWordCount(vocabularyId, userId);
    }
}

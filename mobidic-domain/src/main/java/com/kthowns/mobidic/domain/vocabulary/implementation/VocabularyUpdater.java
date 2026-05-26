package com.kthowns.mobidic.domain.vocabulary.implementation;

import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.domain.vocabulary.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VocabularyUpdater {
    private final VocabularyRepository vocabularyRepository;
    private final VocabularyReader vocabularyReader;

    public void update(UUID userId, UUID vocabularyId, String title, String description) {
        Vocabulary vocabulary = vocabularyReader.readById(vocabularyId, userId);

        vocabularyRepository.update(vocabulary.updateInfo(title, description));
    }

    public void increaseWordCount(UUID vocabularyId) {
        vocabularyRepository.increaseWordCount(vocabularyId);
    }

    public void decreaseWordCount(UUID vocabularyId) {
        vocabularyRepository.decreaseWordCount(vocabularyId);
    }
}

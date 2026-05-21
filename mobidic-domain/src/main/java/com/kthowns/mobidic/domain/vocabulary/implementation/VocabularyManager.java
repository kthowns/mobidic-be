package com.kthowns.mobidic.domain.vocabulary.implementation;

import com.kthowns.mobidic.domain.vocabulary.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VocabularyManager {
    private final VocabularyRepository vocabularyRepository;

    public void increaseWordCount(UUID vocabularyId) {
        vocabularyRepository.increaseWordCount(vocabularyId);
    }

    public void decreaseWordCount(UUID vocabularyId) {
        vocabularyRepository.decreaseWordCount(vocabularyId);
    }
}

package com.kthowns.mobidic.domain.vocabulary.implementation;

import com.kthowns.mobidic.domain.vocabulary.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VocabularyRemover {
    private final VocabularyRepository vocabularyRepository;

    public void remove(UUID vocabularyId, UUID userId) {
        vocabularyRepository.delete(vocabularyId, userId);
    }
}

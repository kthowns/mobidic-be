package com.kthowns.mobidic.domain.vocabulary.implementation;

import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.domain.vocabulary.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VocabularyAppender {
    private final VocabularyRepository vocabularyRepository;

    public Vocabulary append(String title, String description, UUID userId) {
        return vocabularyRepository.append(Vocabulary.create(userId, title, description));
    }
}

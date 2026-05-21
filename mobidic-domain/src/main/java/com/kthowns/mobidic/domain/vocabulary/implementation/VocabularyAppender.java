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

    public void append(String title, String description, UUID userId) {
        vocabularyRepository.append(Vocabulary.builder()
                .title(title)
                .description(description)
                .userId(userId).build());
    }
}

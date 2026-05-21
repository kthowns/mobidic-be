package com.kthowns.mobidic.domain.word.implementation;

import com.kthowns.mobidic.domain.word.model.WordDetail;
import com.kthowns.mobidic.domain.word.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WordReader {
    private final WordRepository wordRepository;

    public List<WordDetail> readDetailsByVocabularyId(UUID userId, UUID vocabularyId, boolean onlyNotLearned) {
        return wordRepository.readDetailsByVocabularyId(userId, vocabularyId, onlyNotLearned);
    }
}

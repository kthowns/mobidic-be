package com.kthowns.mobidic.domain.word.service;

import com.kthowns.mobidic.domain.word.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class WordRemover {
    private final WordRepository wordRepository;

    public void remove(UUID wordId, UUID userId) {
        wordRepository.delete(wordId, userId);
    }
}

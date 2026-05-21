package com.kthowns.mobidic.domain.word.implementation;

import com.kthowns.mobidic.domain.word.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WordRemover {
    private final WordRepository wordRepository;

    public void remove(UUID wordId, UUID userId) {
        wordRepository.delete(wordId, userId);
    }
}

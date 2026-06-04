package com.kthowns.mobidic.domain.word.service;

import com.kthowns.mobidic.domain.word.model.Word;
import com.kthowns.mobidic.domain.word.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class WordUpdater {
    private final WordRepository wordRepository;

    public void update(UUID userId, Word word, String expression) {
        wordRepository.update(word.updateExpression(expression), userId);
    }
}

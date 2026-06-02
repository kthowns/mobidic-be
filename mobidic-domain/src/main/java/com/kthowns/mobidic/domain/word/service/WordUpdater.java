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
    private final WordReader wordReader;

    public void update(UUID userId, UUID wordId, String expression) {
        Word word = wordReader.readByIdAndUserId(wordId, userId);
        wordRepository.update(word.updateExpression(expression), userId);
    }
}

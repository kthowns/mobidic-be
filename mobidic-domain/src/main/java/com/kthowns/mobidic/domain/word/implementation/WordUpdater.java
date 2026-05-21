package com.kthowns.mobidic.domain.word.implementation;

import com.kthowns.mobidic.domain.word.model.Word;
import com.kthowns.mobidic.domain.word.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WordUpdater {
    private final WordRepository wordRepository;

    public void update(UUID wordId, String expression) {
        Word word = Word.builder()
                .id(wordId)
                .expression(expression)
                .build();
        wordRepository.update(word);
    }
}

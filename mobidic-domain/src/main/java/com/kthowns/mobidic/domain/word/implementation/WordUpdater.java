package com.kthowns.mobidic.domain.word.implementation;

import com.kthowns.mobidic.domain.word.model.Word;
import com.kthowns.mobidic.domain.word.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WordUpdater {
    private final WordRepository wordRepository;

    public void update(Word word) {
        wordRepository.update(word);
    }
}

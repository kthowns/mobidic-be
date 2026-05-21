package com.kthowns.mobidic.domain.word.implementation;

import com.kthowns.mobidic.domain.word.model.Word;
import com.kthowns.mobidic.domain.word.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WordAppender {
    private final WordRepository wordRepository;

    public Word append(String expression, UUID vocabularyId) {
        Word word = Word.builder()
                .expression(expression)
                .vocabularyId(vocabularyId)
                .build();
        wordRepository.append(word);
        return word;
    }
}

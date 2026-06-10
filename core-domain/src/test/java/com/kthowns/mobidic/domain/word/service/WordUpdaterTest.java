package com.kthowns.mobidic.domain.word.service;

import com.kthowns.mobidic.domain.word.model.Word;
import com.kthowns.mobidic.domain.word.repository.WordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WordUpdaterTest {

    @Mock
    private WordRepository wordRepository;

    @InjectMocks
    private WordUpdater wordUpdater;

    @Test
    @DisplayName("update 테스트 - 단어 표현 수정 성공")
    void updateTest() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID wordId = UUID.randomUUID();
        String newExpression = "banana";

        Word existingWord = new Word(wordId, UUID.randomUUID(), "apple", null);

        // When
        wordUpdater.update(userId, existingWord, newExpression);

        // Then
        ArgumentCaptor<Word> captor = ArgumentCaptor.forClass(Word.class);
        verify(wordRepository).update(captor.capture(), eq(userId));

        Word updatedWord = captor.getValue();
        assertThat(updatedWord.expression()).isEqualTo(newExpression);
    }
}


package com.kthowns.mobidic.domain.word.implementation;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WordAppenderTest {

    @Mock
    private WordRepository wordRepository;

    @InjectMocks
    private WordAppender target;

    @Test
    @DisplayName("append 테스트 - 단어 추가 성공")
    void appendTest() {
        // Given
        String expression = "apple";
        UUID vocabularyId = UUID.randomUUID();
        Word expectedWord = new Word(UUID.randomUUID(), vocabularyId, expression, null);
        given(wordRepository.append(any(Word.class))).willReturn(expectedWord);

        // When
        Word actualWord = target.append(expression, vocabularyId);

        // Then
        ArgumentCaptor<Word> captor = ArgumentCaptor.forClass(Word.class);
        verify(wordRepository).append(captor.capture());
        
        Word capturedWord = captor.getValue();
        assertThat(capturedWord.expression()).isEqualTo(expression);
        assertThat(capturedWord.vocabularyId()).isEqualTo(vocabularyId);
        
        assertThat(actualWord).isEqualTo(expectedWord);
    }
}

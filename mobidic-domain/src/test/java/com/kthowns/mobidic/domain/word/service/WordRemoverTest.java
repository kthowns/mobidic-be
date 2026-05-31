package com.kthowns.mobidic.domain.word.service;

import com.kthowns.mobidic.domain.word.repository.WordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WordRemoverTest {

    @Mock
    private WordRepository wordRepository;

    @InjectMocks
    private WordRemover wordRemover;

    @Test
    @DisplayName("remove 테스트 - 단어 삭제 성공")
    void removeTest() {
        // Given
        UUID wordId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // When
        wordRemover.remove(wordId, userId);

        // Then
        verify(wordRepository).delete(wordId, userId);
    }
}

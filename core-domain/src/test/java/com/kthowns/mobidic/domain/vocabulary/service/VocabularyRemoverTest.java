package com.kthowns.mobidic.domain.vocabulary.service;

import com.kthowns.mobidic.domain.vocabulary.repository.VocabularyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VocabularyRemoverTest {

    @Mock
    private VocabularyRepository vocabularyRepository;

    @InjectMocks
    private VocabularyRemover vocabularyRemover;

    @Test
    @DisplayName("remove 테스트 - 단어장 삭제 성공")
    void removeTest() {
        // Given
        UUID vocabularyId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // When
        vocabularyRemover.remove(vocabularyId, userId);

        // Then
        verify(vocabularyRepository).delete(vocabularyId, userId);
    }
}

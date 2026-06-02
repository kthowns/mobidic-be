package com.kthowns.mobidic.domain.vocabulary.service;

import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.domain.vocabulary.repository.VocabularyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VocabularyUpdaterTest {

    @Mock
    private VocabularyRepository vocabularyRepository;

    @Mock
    private VocabularyReader vocabularyReader;

    @InjectMocks
    private VocabularyUpdater vocabularyUpdater;

    @Test
    @DisplayName("update 테스트 - 단어장 정보 수정 성공")
    void updateTest() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID vocabId = UUID.randomUUID();
        String newTitle = "새로운 제목";
        String newDescription = "새로운 설명";

        Vocabulary existingVocab = new Vocabulary(vocabId, userId, "기존 제목", "기존 설명", 0, null);
        given(vocabularyReader.readById(vocabId, userId)).willReturn(existingVocab);

        // When
        vocabularyUpdater.update(userId, vocabId, newTitle, newDescription);

        // Then
        ArgumentCaptor<Vocabulary> captor = ArgumentCaptor.forClass(Vocabulary.class);
        verify(vocabularyRepository).update(captor.capture());

        Vocabulary updatedVocab = captor.getValue();
        assertThat(updatedVocab.title()).isEqualTo(newTitle);
        assertThat(updatedVocab.description()).isEqualTo(newDescription);
    }

    @Test
    @DisplayName("increaseWordCount 테스트 - 단어 수 증가 성공")
    void increaseWordCountTest() {
        // Given
        UUID vocabId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // When
        vocabularyUpdater.increaseWordCount(vocabId, userId);

        // Then
        verify(vocabularyRepository).increaseWordCount(vocabId, userId);
    }

    @Test
    @DisplayName("decreaseWordCount 테스트 - 단어 수 감소 성공")
    void decreaseWordCountTest() {
        // Given
        UUID vocabId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // When
        vocabularyUpdater.decreaseWordCount(vocabId, userId);

        // Then
        verify(vocabularyRepository).decreaseWordCount(vocabId, userId);
    }
}

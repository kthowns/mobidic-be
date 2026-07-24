package com.kthowns.mobidic.domain.vocabulary.service;

import com.kthowns.mobidic.domain.global.model.AuditTime;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VocabularyAppenderTest {

    @Mock
    private VocabularyRepository vocabularyRepository;

    @InjectMocks
    private VocabularyAppender vocabularyAppender;

    @Test
    @DisplayName("append 테스트 - 단어장 생성 성공")
    void appendTest() {
        // Given
        UUID userId = UUID.randomUUID();
        String title = "단어장 제목";
        String description = "단어장 설명";

        Vocabulary expectedVocab = new Vocabulary(UUID.randomUUID(), userId, title, description, 0, AuditTime.create());
        given(vocabularyRepository.append(any(Vocabulary.class))).willReturn(expectedVocab);

        // When
        Vocabulary actualVocab = vocabularyAppender.append(title, description, userId);

        // Then
        ArgumentCaptor<Vocabulary> captor = ArgumentCaptor.forClass(Vocabulary.class);
        verify(vocabularyRepository).append(captor.capture());

        Vocabulary capturedVocab = captor.getValue();
        assertThat(capturedVocab.userId()).isEqualTo(userId);
        assertThat(capturedVocab.title()).isEqualTo(title);
        assertThat(capturedVocab.description()).isEqualTo(description);

        assertThat(actualVocab).isEqualTo(expectedVocab);
    }
}

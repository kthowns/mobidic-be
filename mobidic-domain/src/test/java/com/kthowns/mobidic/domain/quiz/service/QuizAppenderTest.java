package com.kthowns.mobidic.domain.quiz.service;

import com.kthowns.mobidic.domain.quiz.model.Quiz;
import com.kthowns.mobidic.domain.quiz.repository.QuizAnswerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QuizAppenderTest {

    @Mock
    private QuizAnswerRepository quizAnswerRepository;

    @InjectMocks
    private QuizAppender quizAppender;

    @Test
    @DisplayName("append 테스트 - 퀴즈 정답 저장 및 토큰 반환 성공")
    void append_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        Quiz quiz = Quiz.builder()
                .id(UUID.randomUUID())
                .wordId(UUID.randomUUID())
                .answer("apple")
                .build();
        long expMillis = 15000L;

        // When
        String token = quizAppender.append(userId, quiz, expMillis);

        // Then
        assertNotNull(token);
        verify(quizAnswerRepository).append(anyString(), any(), eq(expMillis));
    }
}

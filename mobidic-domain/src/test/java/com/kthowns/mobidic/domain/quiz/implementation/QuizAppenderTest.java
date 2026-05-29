package com.kthowns.mobidic.domain.quiz.implementation;

import com.kthowns.mobidic.domain.quiz.repository.QuizRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QuizAppenderTest {

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuizAppender target;

    @Test
    @DisplayName("saveAnswer 테스트 - 정답 저장 성공")
    void saveAnswerTest() {
        // Given
        String key = "quiz:123";
        String answer = "apple";
        long expMillis = 60000L;

        // When
        target.saveAnswer(key, answer, expMillis);

        // Then
        verify(quizRepository).appendAnswer(key, answer, expMillis);
    }
}

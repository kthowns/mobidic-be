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
class QuizRemoverTest {

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuizRemover target;

    @Test
    @DisplayName("removeAnswer 테스트 - 정답 삭제 성공")
    void removeAnswerTest() {
        // Given
        String key = "quiz:123";

        // When
        target.removeAnswer(key);

        // Then
        verify(quizRepository).deleteAnswer(key);
    }
}

package com.kthowns.mobidic.domain.quiz.service;

import com.kthowns.mobidic.domain.quiz.repository.QuizAnswerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QuizRemoverTest {

    @Mock
    private QuizAnswerRepository quizAnswerRepository;

    @InjectMocks
    private QuizRemover quizRemover;

    @Test
    @DisplayName("remove 테스트 - 퀴즈 정답 삭제 호출 성공")
    void remove_Success() {
        // Given
        String token = "token";

        // When
        quizRemover.remove(token);

        // Then
        verify(quizAnswerRepository).remove(anyString());
    }
}

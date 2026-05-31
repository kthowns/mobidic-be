package com.kthowns.mobidic.domain.quiz.service;

import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.quiz.model.QuizAnswer;
import com.kthowns.mobidic.domain.quiz.repository.QuizAnswerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class QuizReaderTest {

    @Mock
    private QuizAnswerRepository quizAnswerRepository;

    @InjectMocks
    private QuizReader quizReader;

    @Test
    @DisplayName("read 테스트 - 존재하는 토큰 조회 성공")
    void read_Success() {
        // Given
        String token = "token";
        QuizAnswer quizAnswer = new QuizAnswer(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "1");
        given(quizAnswerRepository.read(anyString())).willReturn(Optional.of(quizAnswer));

        // When
        QuizAnswer result = quizReader.read(token);

        // Then
        assertEquals(quizAnswer, result);
    }

    @Test
    @DisplayName("read 테스트 - 존재하지 않거나 만료된 토큰 조회 실패 (예외)")
    void read_Fail() {
        // Given
        given(quizAnswerRepository.read(anyString())).willReturn(Optional.empty());

        // When & Then
        assertThrows(ApiException.class, () -> quizReader.read("expired"));
    }
}

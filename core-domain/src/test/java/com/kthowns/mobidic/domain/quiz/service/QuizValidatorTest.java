package com.kthowns.mobidic.domain.quiz.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.quiz.model.QuizAnswer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class QuizValidatorTest {

    @InjectMocks
    private QuizValidator quizValidator;

    @Test
    @DisplayName("validateOwnership 테스트 - 소유자 일치 (통과)")
    void validateOwnership_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        QuizAnswer quizAnswer = QuizAnswer.of(userId, null, null, null);

        // When & Then
        assertThatCode(() -> quizValidator.validateOwnership(quizAnswer, userId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateOwnership 테스트 - 소유자 불일치 (예외)")
    void validateOwnership_Fail() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        QuizAnswer quizAnswer = QuizAnswer.of(otherUserId, null, null, null);

        // When & Then
        assertThatThrownBy(() -> quizValidator.validateOwnership(quizAnswer, userId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.NO_QUIZ.getMessage());
    }
}

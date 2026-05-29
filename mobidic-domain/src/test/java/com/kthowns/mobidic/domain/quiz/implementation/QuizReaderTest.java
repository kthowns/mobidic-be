package com.kthowns.mobidic.domain.quiz.implementation;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.quiz.repository.QuizRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class QuizReaderTest {

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuizReader target;

    @Test
    @DisplayName("readAnswer 테스트 - 정답 조회 성공")
    void readAnswerTest_Success() {
        // Given
        String key = "quiz:123";
        String expectedAnswer = "apple";
        given(quizRepository.getAnswer(key)).willReturn(expectedAnswer);

        // When
        String actualAnswer = target.readAnswer(key);

        // Then
        assertThat(actualAnswer).isEqualTo(expectedAnswer);
    }

    @Test
    @DisplayName("readAnswer 테스트 - 정답 없음 (타임아웃 예외 발생)")
    void readAnswerTest_Fail_Timeout() {
        // Given
        String key = "quiz:123";
        given(quizRepository.getAnswer(key)).willReturn(null);

        // When & Then
        assertThatThrownBy(() -> target.readAnswer(key))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.REQUEST_TIMEOUT.getMessage());
    }
}

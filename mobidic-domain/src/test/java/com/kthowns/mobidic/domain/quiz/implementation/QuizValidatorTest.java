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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class QuizValidatorTest {

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuizValidator target;

    @Test
    @DisplayName("validateQuizKey 테스트 - 유효한 키 (통과)")
    void validateQuizKeyTest_Success() {
        // Given
        String key = "quiz:123";
        given(quizRepository.exists(key)).willReturn(true);

        // When & Then
        assertThatCode(() -> target.validateQuizKey(key))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateQuizKey 테스트 - 키가 null인 경우 (예외)")
    void validateQuizKeyTest_Fail_NullKey() {
        // Given
        String key = null;

        // When & Then
        assertThatThrownBy(() -> target.validateQuizKey(key))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.INVALID_REQUEST.getMessage());
    }

    @Test
    @DisplayName("validateQuizKey 테스트 - 접두사가 잘못된 경우 (예외)")
    void validateQuizKeyTest_Fail_InvalidPrefix() {
        // Given
        String key = "wrong:123";

        // When & Then
        assertThatThrownBy(() -> target.validateQuizKey(key))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.INVALID_REQUEST.getMessage());
    }

    @Test
    @DisplayName("validateQuizKey 테스트 - 존재하지 않는 키 (타임아웃 예외)")
    void validateQuizKeyTest_Fail_NotExists() {
        // Given
        String key = "quiz:123";
        given(quizRepository.exists(key)).willReturn(false);

        // When & Then
        assertThatThrownBy(() -> target.validateQuizKey(key))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.REQUEST_TIMEOUT.getMessage());
    }
}

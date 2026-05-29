package com.kthowns.mobidic.domain.user.implementation;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator target;

    @Test
    @DisplayName("validateEmailDuplication 테스트 - 중복 없음 (통과)")
    void validateEmailDuplicationTest_Success() {
        // Given
        String email = "test@test.com";
        given(userRepository.existsByEmail(email)).willReturn(false);

        // When & Then
        assertThatCode(() -> target.validateEmailDuplication(email))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateEmailDuplication 테스트 - 중복 발생 (예외)")
    void validateEmailDuplicationTest_Fail() {
        // Given
        String email = "test@test.com";
        given(userRepository.existsByEmail(email)).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> target.validateEmailDuplication(email))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.DUPLICATED_EMAIL.getMessage());
    }

    @Test
    @DisplayName("validateNicknameDuplication 테스트 - 중복 없음 (통과)")
    void validateNicknameDuplicationTest_Success() {
        // Given
        String nickname = "testNick";
        given(userRepository.existsByNickname(nickname)).willReturn(false);

        // When & Then
        assertThatCode(() -> target.validateNicknameDuplication(nickname))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateNicknameDuplication 테스트 - 중복 발생 (예외)")
    void validateNicknameDuplicationTest_Fail() {
        // Given
        String nickname = "testNick";
        given(userRepository.existsByNickname(nickname)).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> target.validateNicknameDuplication(nickname))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.DUPLICATED_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("validateNicknameUpdateDuplication 테스트 - 중복 없음 (통과)")
    void validateNicknameUpdateDuplicationTest_Success() {
        // Given
        String nickname = "newNick";
        UUID userId = UUID.randomUUID();
        given(userRepository.existsByNicknameAndIdNot(nickname, userId)).willReturn(false);

        // When & Then
        assertThatCode(() -> target.validateNicknameUpdateDuplication(nickname, userId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validatePassword 테스트 - 유효한 비밀번호 (통과)")
    void validatePasswordTest_Success() {
        // When & Then
        assertThatCode(() -> target.validatePassword("password123!"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validatePassword 테스트 - 짧은 비밀번호 (예외)")
    void validatePasswordTest_Fail() {
        // When & Then
        assertThatThrownBy(() -> target.validatePassword("short"))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("비밀번호는 8자 이상이어야 합니다.");
    }
}

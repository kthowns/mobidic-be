package com.kthowns.mobidic.domain.user.service;

import com.kthowns.mobidic.domain.user.client.PasswordEncoderClient;
import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.model.UserRole;
import com.kthowns.mobidic.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserAppenderTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoderClient passwordEncoderClient;

    @InjectMocks
    private UserAppender userAppender;

    @Test
    @DisplayName("append 테스트 - 일반 사용자 생성 성공")
    void appendTest() {
        // Given
        String email = "test@test.com";
        String nickname = "test";
        String password = "plainPassword";
        String encodedPassword = "encodedPassword";
        UserRole role = UserRole.USER;

        given(passwordEncoderClient.encode(password)).willReturn(encodedPassword);
        User expectedUser = User.create(email, nickname, encodedPassword, role);
        given(userRepository.append(any(User.class))).willReturn(expectedUser);

        // When
        User actualUser = userAppender.append(email, nickname, password, role);

        // Then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).append(captor.capture());

        User capturedUser = captor.getValue();
        assertThat(capturedUser.email()).isEqualTo(email);
        assertThat(capturedUser.nickname()).isEqualTo(nickname);
        assertThat(capturedUser.password()).isEqualTo(encodedPassword);
        assertThat(capturedUser.role()).isEqualTo(role);

        assertThat(actualUser).isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("appendKakao 테스트 - 카카오 사용자 생성 성공")
    void appendKakaoTest() {
        // Given
        Long kakaoId = 12345L;
        String email = "kakao@test.com";
        String nickname = "kakao";
        String password = "plainPassword";
        String encodedPassword = "encodedPassword";
        UserRole role = UserRole.USER;

        given(passwordEncoderClient.encode(password)).willReturn(encodedPassword);
        User expectedUser = User.createKakao(kakaoId, email, nickname, encodedPassword, role);
        given(userRepository.append(any(User.class))).willReturn(expectedUser);

        // When
        User actualUser = userAppender.appendKakao(kakaoId, email, nickname, password, role);

        // Then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).append(captor.capture());

        User capturedUser = captor.getValue();
        assertThat(capturedUser.kakaoId()).isEqualTo(kakaoId);
        assertThat(capturedUser.email()).isEqualTo(email);
        assertThat(capturedUser.nickname()).isEqualTo(nickname);
        assertThat(capturedUser.password()).isEqualTo(encodedPassword);

        assertThat(actualUser).isEqualTo(expectedUser);
    }
}

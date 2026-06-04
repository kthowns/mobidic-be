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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class UserUpdaterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserReader userReader;

    @Mock
    private PasswordEncoderClient passwordEncoderClient;

    @InjectMocks
    private UserUpdater userUpdater;

    @Test
    @DisplayName("update 테스트 - 닉네임과 비밀번호 모두 수정")
    void updateTest_Both() {
        // Given
        UUID userId = UUID.randomUUID();
        String newNickname = "newNick";
        String newPassword = "newPassword";
        String encodedPassword = "encodedPassword";

        User existingUser = new User(userId, null, "test@test.com", "oldNick", "oldPass", UserRole.USER, true, LocalDateTime.now(), null);
        given(userReader.readById(userId)).willReturn(existingUser);
        given(passwordEncoderClient.encode(newPassword)).willReturn(encodedPassword);

        User updatedUser = existingUser.updateProfile(newNickname, encodedPassword);
        given(userRepository.update(any(User.class))).willReturn(updatedUser);

        // When
        User result = userUpdater.update(userId, newNickname, newPassword);

        // Then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).update(captor.capture());

        User capturedUser = captor.getValue();
        assertThat(capturedUser.nickname()).isEqualTo(newNickname);
        assertThat(capturedUser.password()).isEqualTo(encodedPassword);

        assertThat(result).isEqualTo(updatedUser);
    }

    @Test
    @DisplayName("update 테스트 - 닉네임만 수정 (비밀번호 null)")
    void updateTest_NicknameOnly() {
        // Given
        UUID userId = UUID.randomUUID();
        String newNickname = "newNick";
        String newPassword = null;

        User existingUser = new User(userId, null, "test@test.com", "oldNick", "oldPass", UserRole.USER, true, LocalDateTime.now(), null);
        given(userReader.readById(userId)).willReturn(existingUser);

        User updatedUser = existingUser.updateProfile(newNickname, null);
        given(userRepository.update(any(User.class))).willReturn(updatedUser);

        // When
        User result = userUpdater.update(userId, newNickname, newPassword);

        // Then
        verifyNoInteractions(passwordEncoderClient);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).update(captor.capture());

        User capturedUser = captor.getValue();
        assertThat(capturedUser.nickname()).isEqualTo(newNickname);
        assertThat(capturedUser.password()).isEqualTo("oldPass"); // 비밀번호 유지

        assertThat(result).isEqualTo(updatedUser);
    }
}


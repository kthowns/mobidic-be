package com.kthowns.mobidic.domain.user.service;

import com.kthowns.mobidic.common.code.AuthResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.model.UserRole;
import com.kthowns.mobidic.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserReaderTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserReader userReader;

    @Test
    @DisplayName("readById 테스트 - 조회 성공")
    void readByIdTest_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        User expectedUser = new User(userId, null, "test@test.com", "test", "pass", UserRole.USER, true, LocalDateTime.now(), null);
        given(userRepository.readById(userId)).willReturn(Optional.of(expectedUser));

        // When
        User actualUser = userReader.readById(userId);

        // Then
        assertThat(actualUser).isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("readById 테스트 - 조회 실패 (예외 발생)")
    void readByIdTest_Fail() {
        // Given
        UUID userId = UUID.randomUUID();
        given(userRepository.readById(userId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userReader.readById(userId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(AuthResponseCode.NO_USER.getMessage());
    }
}

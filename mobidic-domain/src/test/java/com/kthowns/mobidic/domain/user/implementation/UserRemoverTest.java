package com.kthowns.mobidic.domain.user.implementation;

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

@ExtendWith(MockitoExtension.class)
class UserRemoverTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserReader userReader;

    @InjectMocks
    private UserRemover target;

    @Test
    @DisplayName("deactivate 테스트 - 사용자 비활성화 성공")
    void deactivateTest() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = new User(userId, null, "test@test.com", "test", "pass", UserRole.USER, true, LocalDateTime.now(), null);
        given(userReader.readById(userId)).willReturn(user);

        User deactivatedUser = user.deactivate();
        given(userRepository.update(any(User.class))).willReturn(deactivatedUser);

        // When
        User result = target.deactivate(userId);

        // Then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).update(captor.capture());

        User updatedUser = captor.getValue();
        assertThat(updatedUser.isActive()).isFalse();
        assertThat(updatedUser.deactivatedAt()).isNotNull();

        assertThat(result).isEqualTo(deactivatedUser);
    }
}


package com.kthowns.mobidic.api.service;

import com.kthowns.mobidic.domain.user.client.PasswordEncoderClient;
import com.kthowns.mobidic.domain.user.implementation.*;
import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.model.UserRole;
import com.kthowns.mobidic.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserReader userReader;
    @Mock
    private UserAppender userAppender;
    @Mock
    private UserUpdater userUpdater;
    @Mock
    private UserRemover userRemover;
    @Mock
    private UserValidator userValidator;
    @Mock
    private PasswordEncoderClient passwordEncoderClient;

    private final UUID userId = UUID.randomUUID();

    @Test
    @DisplayName("[UserService] Register user success")
    void registerUserSuccess() {
        // given
        String email = "test@test.com";
        String nickname = "test";
        String password = "password123!";
        String encodedPassword = "encodedPassword";
        User user = new User(userId, null, email, nickname, encodedPassword, UserRole.USER, true, LocalDateTime.now(), null);

        given(passwordEncoderClient.encode(password)).willReturn(encodedPassword);
        given(userAppender.append(email, nickname, encodedPassword, UserRole.USER)).willReturn(user);

        // when
        User result = userService.registerUser(email, nickname, password);

        // then
        verify(userValidator).validateEmailDuplication(email);
        verify(userValidator).validateNicknameDuplication(nickname);
        verify(userValidator).validatePassword(password);
        assertEquals(user, result);
    }

    @Test
    @DisplayName("[UserService] Register kakao user success")
    void registerKakaoUserSuccess() {
        // given
        Long kakaoId = 12345L;
        String email = "kakao@test.com";
        String nickname = "kakaoUser";
        User user = new User(userId, kakaoId, email, nickname, "pw", UserRole.USER, true, LocalDateTime.now(), null);

        given(passwordEncoderClient.encode(org.mockito.ArgumentMatchers.anyString())).willReturn("encoded");
        given(userAppender.appendKakao(org.mockito.ArgumentMatchers.eq(kakaoId), org.mockito.ArgumentMatchers.eq(email), org.mockito.ArgumentMatchers.eq(nickname), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.eq(UserRole.USER))).willReturn(user);

        // when
        User result = userService.registerKakaoUser(kakaoId, email, nickname);

        // then
        assertEquals(user, result);
    }

    @Test
    @DisplayName("[UserService] Update user success")
    void updateUserSuccess() {
        // given
        String newNickname = "newNick";
        String newPassword = "newPassword123!";
        String encodedPassword = "encodedNewPassword";
        User user = new User(userId, null, "test@test.com", newNickname, encodedPassword, UserRole.USER, true, LocalDateTime.now(), null);

        given(passwordEncoderClient.encode(newPassword)).willReturn(encodedPassword);
        given(userUpdater.update(userId, newNickname, encodedPassword)).willReturn(user);

        // when
        User result = userService.updateUser(userId, newNickname, newPassword);

        // then
        verify(userValidator).validateNicknameUpdateDuplication(newNickname, userId);
        verify(userValidator).validatePassword(newPassword);
        assertEquals(user, result);
    }

    @Test
    @DisplayName("[UserService] Deactivate user success")
    void deactivateUserSuccess() {
        // given
        User user = new User(userId, null, "test@test.com", "nick", "pw", UserRole.USER, false, LocalDateTime.now(), LocalDateTime.now());
        given(userRemover.deactivate(userId)).willReturn(user);

        // when
        User result = userService.deactivateUser(userId);

        // then
        assertEquals(user, result);
    }

    @Test
    @DisplayName("[UserService] Get user by id success")
    void getUserByIdSuccess() {
        // given
        User user = new User(userId, null, "test@test.com", "nick", "pw", UserRole.USER, true, LocalDateTime.now(), null);
        given(userReader.readById(userId)).willReturn(user);

        // when
        User result = userService.getUserById(userId);

        // then
        assertEquals(user, result);
    }
}

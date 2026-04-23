package com.kimtaeyang.mobidic.service;

import com.kimtaeyang.mobidic.config.ServiceTestConfig;
import com.kimtaeyang.mobidic.user.dto.SignUpRequestDto;
import com.kimtaeyang.mobidic.user.dto.UpdateUserRequestDto;
import com.kimtaeyang.mobidic.user.dto.UserDto;
import com.kimtaeyang.mobidic.user.entity.User;
import com.kimtaeyang.mobidic.user.repository.UserRepository;
import com.kimtaeyang.mobidic.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserService.class, ServiceTestConfig.class})
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "jwt.secret=f825308ac5df56907db5835775baf3e4594526f127cb8d9bca70b435d596d424",
        "jwt.exp=3600000"
})
class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String UID = "9f81b0d7-2f8e-4ad3-ae18-41c73dc71b39";

    @Test
    @DisplayName("[AuthService] Join success")
    void signUpTestSuccess() {
        // given
        String rawPassword = "test1234";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        SignUpRequestDto request = SignUpRequestDto.builder()
                .email("user@example.com")
                .nickname("tester")
                .password(rawPassword)
                .build();

        User userToReturn = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(encodedPassword)
                .build();

        // mocking
        Mockito.when(userRepository.existsByNickname(anyString()))
                .thenReturn(false);
        Mockito.when(userRepository.existsByEmail(anyString()))
                .thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(userToReturn);

        // when
        userService.signUp(request);

        // then
        Mockito.verify(userRepository).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("[UserService] Update user nickname success")
    @WithMockUser(username = UID)
    void updateUserNicknameSuccess() {
        resetMock();

        User defaultUser = User.builder()
                .id(UUID.fromString(UID))
                .email("test@test.com")
                .nickname("test")
                .password(passwordEncoder.encode("testTest1"))
                .build();

        UpdateUserRequestDto request = UpdateUserRequestDto.builder()
                .nickname("test2")
                .build();

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        //given
        given(userRepository.existsByNicknameAndIdNot(anyString(), any(UUID.class)))
                .willReturn(false);
        given(userRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(defaultUser));
        given(userRepository.save(any(User.class)))
                .willAnswer(invocation -> {
                    User userArg = invocation.getArgument(0);
                    userArg.setNickname(request.getNickname());
                    return userArg;
                });

        //when
        UserDto response = userService.updateUser(defaultUser, request, UUID.randomUUID().toString());

        //then
        assertEquals(UUID.fromString(UID), response.getId());
        assertEquals(request.getNickname(), response.getNickname());
    }

    @Test
    @DisplayName("[UserService] Update user password success")
    @WithMockUser(username = UID)
    void updateUserPasswordSuccess() {
        resetMock();

        User defaultUser = User.builder()
                .id(UUID.fromString(UID))
                .email("test@test.com")
                .nickname("test")
                .password(passwordEncoder.encode("testTest1"))
                .build();

        UpdateUserRequestDto request = UpdateUserRequestDto.builder()
                .password("SomePassword")
                .build();

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        //given
        given(userRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(defaultUser));
        given(userRepository.save(any(User.class)))
                .willAnswer(invocation -> {
                    User userArg = invocation.getArgument(0);
                    userArg.setPassword(passwordEncoder.encode(
                            request.getPassword()));
                    return userArg;
                });

        //when
        UserDto response = userService.updateUser(defaultUser, request, UUID.randomUUID().toString());

        //then
        assertThat(passwordEncoder.matches(request.getPassword(), "SomePassword"));
    }

    private void resetMock() {
        Mockito.reset(userRepository);
    }
}
package com.kimtaeyang.mobidic.service;

import com.kimtaeyang.mobidic.auth.service.AuthService;
import com.kimtaeyang.mobidic.user.dto.UserDto;
import com.kimtaeyang.mobidic.user.dto.UpdateNicknameRequestDto;
import com.kimtaeyang.mobidic.user.dto.UpdatePasswordRequestDto;
import com.kimtaeyang.mobidic.user.entity.User;
import com.kimtaeyang.mobidic.user.repository.UserRepository;
import com.kimtaeyang.mobidic.security.JwtBlacklistService;
import com.kimtaeyang.mobidic.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@TestPropertySource(properties = {
        "jwt.secret=qwerqwerqwerqwerqwerqwerqwerqwer",
        "jwt.exp=3600"
})
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserService.class, UserServiceTest.TestConfig.class})
@ActiveProfiles("dev")
class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    AuthService authService;

    private static final String UID = "9f81b0d7-2f8e-4ad3-ae18-41c73dc71b39";

    @Test
    @DisplayName("[MemberService] Get member detail success")
    @WithMockUser(username = UID)
    void getUserDetailByIdSuccess() {
        resetMock();

        User user = User.builder()
                .id(UUID.fromString(UID))
                .email("test@test.com")
                .nickname("test")
                .password(passwordEncoder.encode("test"))
                .build();

        //given
        given(userRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(user));

        //when
        UserDto response = userService.getUserDetailById(UUID.fromString(UID));

        //then
        assertEquals(user.getNickname(), response.getNickname());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(UUID.fromString(UID), response.getId());
    }

    @Test
    @DisplayName("[MemberService] Update member nickname success")
    @WithMockUser(username = UID)
    void updateUserNicknameSuccess() {
        resetMock();

        User defaultUser = User.builder()
                .id(UUID.fromString(UID))
                .email("test@test.com")
                .nickname("test")
                .password(passwordEncoder.encode("testTest1"))
                .build();

        UpdateNicknameRequestDto request = UpdateNicknameRequestDto.builder()
                .nickname("test2")
                .build();

        ArgumentCaptor<User> memberCaptor =
                ArgumentCaptor.forClass(User.class);

        //given
        given(userRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(defaultUser));
        given(userRepository.countByNicknameAndIdNot(anyString(), any(UUID.class)))
                .willReturn(0);
        given(userRepository.save(any(User.class)))
                .willAnswer(invocation -> {
                    User userArg = invocation.getArgument(0);
                    userArg.setNickname(request.getNickname());
                    return userArg;
                });

        //when
        UserDto response = userService.updateUserNickname(UUID.fromString(UID), request);

        //then
        verify(userRepository, times(1))
                .save(memberCaptor.capture());

        assertEquals(request.getNickname(), response.getNickname());
        assertEquals(UUID.fromString(UID), response.getId());
    }

    @Test
    @DisplayName("[MemberService] Update member password success")
    @WithMockUser(username = UID)
    void updateUserPasswordSuccess() {
        resetMock();

        User defaultUser = User.builder()
                .id(UUID.fromString(UID))
                .email("test@test.com")
                .nickname("test")
                .password(passwordEncoder.encode("testTest1"))
                .build();

        UpdatePasswordRequestDto request = UpdatePasswordRequestDto.builder()
                .password("testTest2")
                .build();

        ArgumentCaptor<User> memberCaptor =
                ArgumentCaptor.forClass(User.class);

        //given
        given(userRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(defaultUser));
        given(authService.logout(any(UUID.class), anyString()))
                .willReturn(Mockito.mock(UserDto.class));
        given(userRepository.save(any(User.class)))
                .willAnswer(invocation -> {
                    User userArg = invocation.getArgument(0);
                    userArg.setPassword(passwordEncoder.encode(
                            request.getPassword()));
                    return userArg;
                });

        //when
        userService.updateUserPassword(UUID.fromString(UID), request, UUID.randomUUID().toString());

        //then
        verify(userRepository, times(1))
                .save(memberCaptor.capture());
        User savedUser = memberCaptor.getValue();

        assertThat(passwordEncoder.matches(request.getPassword(), savedUser.getPassword()));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserRepository memberRepository() {
            return Mockito.mock(UserRepository.class);
        }

        @Bean
        public JwtBlacklistService jwtBlacklistService() {
            return Mockito.mock(JwtBlacklistService.class);
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthService authService() {
            return Mockito.mock(AuthService.class);
        }
    }

    private void resetMock() {
        Mockito.reset(userRepository);
    }
}
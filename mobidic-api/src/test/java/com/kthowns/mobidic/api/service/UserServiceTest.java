package com.kthowns.mobidic.api.service;

import com.kthowns.mobidic.api.config.ServiceTestConfig;
import com.kthowns.mobidic.api.user.dto.request.SignUpRequestDto;
import com.kthowns.mobidic.api.user.dto.request.UpdateUserRequestDto;
import com.kthowns.mobidic.api.user.dto.response.UserDto;
import com.kthowns.mobidic.storage.user.jpaentity.UserJpaEntity;
import com.kthowns.mobidic.storage.user.jparepository.UserJpaRepository;
import com.kthowns.mobidic.domain.user.service.UserService;
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
    private UserJpaRepository userJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String UID = "9f81b0d7-2f8e-4ad3-ae18-41c73dc71b39";

    @Test
    @DisplayName("[AuthService] Join success")
    void registerUserTestSuccess() {
        // given
        String rawPassword = "test1234";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        SignUpRequestDto request = SignUpRequestDto.builder()
                .email("user@example.com")
                .nickname("tester")
                .password(rawPassword)
                .build();

        UserJpaEntity userJpaEntityToReturn = UserJpaEntity.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(encodedPassword)
                .build();

        // mocking
        Mockito.when(userJpaRepository.existsByNickname(anyString()))
                .thenReturn(false);
        Mockito.when(userJpaRepository.existsByEmail(anyString()))
                .thenReturn(false);
        Mockito.when(userJpaRepository.save(Mockito.any(UserJpaEntity.class)))
                .thenReturn(userJpaEntityToReturn);

        // when
        userService.registerUser(request);

        // then
        Mockito.verify(userJpaRepository).save(Mockito.any(UserJpaEntity.class));
    }

    @Test
    @DisplayName("[UserService] Update user nickname success")
    @WithMockUser(username = UID)
    void updateUserNicknameSuccess() {
        resetMock();

        UserJpaEntity defaultUserJpaEntity = UserJpaEntity.builder()
                .id(UUID.fromString(UID))
                .email("test@test.com")
                .nickname("test")
                .password(passwordEncoder.encode("testTest1"))
                .build();

        UpdateUserRequestDto request = UpdateUserRequestDto.builder()
                .nickname("test2")
                .build();

        ArgumentCaptor<UserJpaEntity> userCaptor =
                ArgumentCaptor.forClass(UserJpaEntity.class);

        //given
        given(userJpaRepository.existsByNicknameAndIdNot(anyString(), any(UUID.class)))
                .willReturn(false);
        given(userJpaRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(defaultUserJpaEntity));
        given(userJpaRepository.save(any(UserJpaEntity.class)))
                .willAnswer(invocation -> {
                    UserJpaEntity userJpaEntityArg = invocation.getArgument(0);
                    userJpaEntityArg.setNickname(request.getNickname());
                    return userJpaEntityArg;
                });

        //when
        UserDto response = userService.updateUser(defaultUserJpaEntity, request, UUID.randomUUID().toString());

        //then
        assertEquals(UUID.fromString(UID), response.getId());
        assertEquals(request.getNickname(), response.getNickname());
    }

    @Test
    @DisplayName("[UserService] Update user password success")
    @WithMockUser(username = UID)
    void updateUserPasswordSuccess() {
        resetMock();

        UserJpaEntity defaultUserJpaEntity = UserJpaEntity.builder()
                .id(UUID.fromString(UID))
                .email("test@test.com")
                .nickname("test")
                .password(passwordEncoder.encode("testTest1"))
                .build();

        UpdateUserRequestDto request = UpdateUserRequestDto.builder()
                .password("SomePassword")
                .build();

        ArgumentCaptor<UserJpaEntity> userCaptor =
                ArgumentCaptor.forClass(UserJpaEntity.class);

        //given
        given(userJpaRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(defaultUserJpaEntity));
        given(userJpaRepository.save(any(UserJpaEntity.class)))
                .willAnswer(invocation -> {
                    UserJpaEntity userJpaEntityArg = invocation.getArgument(0);
                    userJpaEntityArg.setPassword(passwordEncoder.encode(
                            request.getPassword()));
                    return userJpaEntityArg;
                });

        //when
        UserDto response = userService.updateUser(defaultUserJpaEntity, request, UUID.randomUUID().toString());

        //then
        assertThat(passwordEncoder.matches(request.getPassword(), "SomePassword"));
    }

    private void resetMock() {
        Mockito.reset(userJpaRepository);
    }
}
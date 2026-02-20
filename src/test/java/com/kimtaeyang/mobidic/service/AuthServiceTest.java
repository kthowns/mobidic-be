package com.kimtaeyang.mobidic.service;

import com.kimtaeyang.mobidic.auth.dto.LoginDto;
import com.kimtaeyang.mobidic.auth.dto.SignUpRequestDto;
import com.kimtaeyang.mobidic.auth.service.AuthService;
import com.kimtaeyang.mobidic.config.ServiceTestConfig;
import com.kimtaeyang.mobidic.security.jwt.JwtProvider;
import com.kimtaeyang.mobidic.user.dto.UserDto;
import com.kimtaeyang.mobidic.user.entity.User;
import com.kimtaeyang.mobidic.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AuthService.class, ServiceTestConfig.class})
@ActiveProfiles("dev")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository; // mock

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Test
    @DisplayName("[AuthService] Join success")
    void joinTestSuccess() {
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
        Mockito.when(userRepository.countByNickname(anyString()))
                .thenReturn(0);
        Mockito.when(userRepository.countByEmail(anyString()))
                .thenReturn(0);
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(userToReturn);

        // when
        UserDto response = authService.join(request);

        // then
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getNickname(), response.getNickname());
        Mockito.verify(userRepository).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("[AuthService] Login success")
    void loginTestSuccess() {
        String rawPassword = "test1234";

        LoginDto.Request request = LoginDto.Request.builder()
                .email("user@example.com")
                .password(rawPassword)
                .build();

        User principal = User.builder()
                .id(UUID.randomUUID())
                .email(request.getEmail())
                .build();

        Authentication mockAuth = Mockito.mock(Authentication.class);

        // given
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        Mockito.when(mockAuth.getPrincipal())
                .thenReturn(principal);

        // when
        String token = authService.login(request).getToken();

        // then
        assertEquals(principal.getId(), jwtProvider.getIdFromToken(token));
        assertThat(jwtProvider.validateToken(token));
    }

    @Test
    @DisplayName("[AuthService] Login failed")
    void loginTestFail() {
        String rawPassword = "test1234";

        LoginDto.Request request = LoginDto.Request.builder()
                .email("user@example.com")
                .password(rawPassword)
                .build();

        // given
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(BadCredentialsException.class);

        //when
        Throwable e = assertThrows(Exception.class, () -> authService.login(request));

        // then
        assertEquals(e.getMessage(), e.getMessage());
    }
}

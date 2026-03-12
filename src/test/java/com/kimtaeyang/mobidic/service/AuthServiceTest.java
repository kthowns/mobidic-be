package com.kimtaeyang.mobidic.service;

import com.kimtaeyang.mobidic.auth.dto.LoginRequest;
import com.kimtaeyang.mobidic.auth.service.AuthService;
import com.kimtaeyang.mobidic.config.ServiceTestConfig;
import com.kimtaeyang.mobidic.security.jwt.JwtProvider;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AuthService.class, ServiceTestConfig.class})
@TestPropertySource(properties = {
        "jwt.secret=f825308ac5df56907db5835775baf3e4594526f127cb8d9bca70b435d596d424",
        "jwt.exp=3600000"
})
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
    @DisplayName("[AuthService] Login success")
    void loginTestSuccess() {
        String rawPassword = "test1234";

        LoginRequest request = LoginRequest.builder()
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
        String token = authService.login(request).getAccessToken();

        // then
        assertEquals(principal.getId(), jwtProvider.getIdFromToken(token));
        assertThat(jwtProvider.validateToken(token));
    }

    @Test
    @DisplayName("[AuthService] Login failed")
    void loginTestFail() {
        String rawPassword = "test1234";

        LoginRequest request = LoginRequest.builder()
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

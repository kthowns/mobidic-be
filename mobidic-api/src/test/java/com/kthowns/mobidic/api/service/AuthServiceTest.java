package com.kthowns.mobidic.api.service;

import com.kthowns.mobidic.api.auth.dto.request.LoginRequest;
import com.kthowns.mobidic.api.auth.dto.response.LoginResponse;
import com.kthowns.mobidic.api.auth.model.AuthUser;
import com.kthowns.mobidic.api.auth.service.AuthService;
import com.kthowns.mobidic.api.security.jwt.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("[AuthService] Login success")
    void loginSuccess() {
        // given
        LoginRequest request = new LoginRequest("test@test.com", "password123!");
        AuthUser authUser = mock(AuthUser.class);
        Authentication authentication = mock(Authentication.class);
        String expectedToken = "testToken";

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(authUser);
        given(authUser.getId()).willReturn(UUID.randomUUID());
        given(authUser.getRole()).willReturn("USER");
        given(jwtProvider.generateToken(any(UUID.class), any(String.class))).willReturn(expectedToken);

        // when
        LoginResponse response = authService.login(request);

        // then
        assertEquals(expectedToken, response.getAccessToken());
    }

    @Test
    @DisplayName("[AuthService] Login fail - Bad credentials")
    void loginFail() {
        // given
        LoginRequest request = new LoginRequest("test@test.com", "wrongPassword");
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new BadCredentialsException("Invalid password"));

        // when & then
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}

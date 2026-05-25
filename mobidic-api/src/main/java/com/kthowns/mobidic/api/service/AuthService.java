package com.kthowns.mobidic.api.service;

import com.kthowns.mobidic.api.auth.dto.request.LoginRequest;
import com.kthowns.mobidic.api.auth.dto.response.LoginResponse;
import com.kthowns.mobidic.api.auth.model.AuthUser;
import com.kthowns.mobidic.api.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public LoginResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        AuthUser authUser = (AuthUser) auth.getPrincipal();

        return LoginResponse.builder()
                .accessToken(jwtProvider.generateToken(authUser.getId(), authUser.getRole()))
                .build();
    }
}

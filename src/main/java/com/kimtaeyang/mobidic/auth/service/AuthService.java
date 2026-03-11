package com.kimtaeyang.mobidic.auth.service;

import com.kimtaeyang.mobidic.auth.dto.LoginRequest;
import com.kimtaeyang.mobidic.auth.dto.LoginResponse;
import com.kimtaeyang.mobidic.security.jwt.JwtBlacklistService;
import com.kimtaeyang.mobidic.security.jwt.JwtProvider;
import com.kimtaeyang.mobidic.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final JwtBlacklistService jwtBlacklistService;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = (User) auth.getPrincipal();

        return LoginResponse.builder()
                .accessToken(jwtProvider.generateToken(user.getId()))
                .build();
    }

    public void logout(String token) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auth.setAuthenticated(false); //인증 Context 초기화

        jwtBlacklistService.logoutToken(token); //Redis 블랙리스트에 토큰 추가
    }
}
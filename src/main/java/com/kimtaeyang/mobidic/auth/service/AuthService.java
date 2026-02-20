package com.kimtaeyang.mobidic.auth.service;

import com.kimtaeyang.mobidic.auth.dto.SignUpRequestDto;
import com.kimtaeyang.mobidic.auth.dto.LoginDto;
import com.kimtaeyang.mobidic.user.dto.UserDto;
import com.kimtaeyang.mobidic.user.entity.User;
import com.kimtaeyang.mobidic.common.exception.ApiException;
import com.kimtaeyang.mobidic.user.repository.UserRepository;
import com.kimtaeyang.mobidic.security.jwt.JwtBlacklistService;
import com.kimtaeyang.mobidic.security.jwt.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.kimtaeyang.mobidic.common.code.AuthResponseCode.NO_MEMBER;
import static com.kimtaeyang.mobidic.common.code.GeneralResponseCode.DUPLICATED_EMAIL;
import static com.kimtaeyang.mobidic.common.code.GeneralResponseCode.DUPLICATED_NICKNAME;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final JwtBlacklistService jwtBlacklistService;

    @Transactional(readOnly = true)
    public LoginDto.Response login(LoginDto.Request request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = (User) auth.getPrincipal();

        return LoginDto.Response.builder()
                .memberId(user.getId().toString())
                .token(jwtProvider.generateToken(user.getId()))
                .build();
    }

    @Transactional
    public UserDto join(@Valid SignUpRequestDto request) {
        if (userRepository.countByNickname(request.getNickname()) > 0) {
            throw new ApiException(DUPLICATED_NICKNAME);
        }

        if (userRepository.countByEmail(request.getEmail()) > 0) {
            throw new ApiException(DUPLICATED_EMAIL);
        }

        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        return UserDto.fromEntity(userRepository.save(user));
    }

    @PreAuthorize("@userAccessHandler.ownershipCheck(#memberId)")
    public UserDto logout(UUID memberId, String token) {
        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(NO_MEMBER));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auth.setAuthenticated(false); //인증 Context 초기화

        jwtBlacklistService.logoutToken(token); //Redis 블랙리스트에 토큰 추가

        return UserDto.fromEntity(user);
    }
}
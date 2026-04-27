package com.kimtaeyang.mobidic.user.service;

import com.kimtaeyang.mobidic.auth.dto.KakaoUserInfo;
import com.kimtaeyang.mobidic.auth.service.AuthService;
import com.kimtaeyang.mobidic.common.code.GeneralResponseCode;
import com.kimtaeyang.mobidic.common.exception.ApiException;
import com.kimtaeyang.mobidic.security.jwt.JwtBlacklistService;
import com.kimtaeyang.mobidic.user.dto.SignUpRequestDto;
import com.kimtaeyang.mobidic.user.dto.UpdateUserRequestDto;
import com.kimtaeyang.mobidic.user.dto.UserDto;
import com.kimtaeyang.mobidic.user.entity.User;
import com.kimtaeyang.mobidic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.kimtaeyang.mobidic.common.code.AuthResponseCode.NO_USER;
import static com.kimtaeyang.mobidic.common.code.GeneralResponseCode.DUPLICATED_EMAIL;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtBlacklistService jwtBlacklistService;
    private final AuthService authService;

    @Transactional
    public User registerUser(SignUpRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(DUPLICATED_EMAIL);
        }

        return userRepository.save(
                User.builder()
                        .email(request.getEmail())
                        .nickname(request.getNickname())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .build()
        );
    }

    @Transactional
    public User registerKakaoUser(KakaoUserInfo kakaoUserInfo) {
        return userRepository.save(
                User.builder()
                        .kakaoId(kakaoUserInfo.getId())
                        .email(kakaoUserInfo.getKakaoAccount().getEmail())
                        .nickname(kakaoUserInfo.getKakaoAccount().getProfile().getNickname())
                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .build());
    }

    @Transactional
    public UserDto updateUser(User user, UpdateUserRequestDto request, String token) {
        User updateUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ApiException(NO_USER));

        if (request.getNickname() != null) {
            if (userRepository.existsByNicknameAndIdNot(request.getNickname(), user.getId())) {
                throw new ApiException(GeneralResponseCode.DUPLICATED_NICKNAME);
            }
            updateUser.setNickname(request.getNickname());
        }

        if (request.getPassword() != null) {
            updateUser.setPassword(passwordEncoder.encode(request.getPassword()));
            authService.logout(token);
        }

        return UserDto.fromEntity(updateUser);
    }

    @Transactional
    public UserDto deactivateUser(User user, String token) {
        user.setDeactivatedAt(LocalDateTime.now());
        user.setIsActive(false);

        jwtBlacklistService.withdrawToken(token);

        return UserDto.fromEntity(user);
    }
}

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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.kimtaeyang.mobidic.common.code.AuthResponseCode.NO_USER;
import static com.kimtaeyang.mobidic.common.code.GeneralResponseCode.DUPLICATED_EMAIL;
import static com.kimtaeyang.mobidic.common.code.GeneralResponseCode.DUPLICATED_NICKNAME;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtBlacklistService jwtBlacklistService;
    private final AuthService authService;

    @Transactional
    public User signUp(@Valid SignUpRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(DUPLICATED_EMAIL);
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new ApiException(DUPLICATED_NICKNAME);
        }

        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        return userRepository.save(user);
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

    @Transactional
    public User getUserOrCreate(KakaoUserInfo kakaoUserInfo) {
        return userRepository.findByKakaoId(kakaoUserInfo.getId())
                .orElseGet(() -> userRepository.save(
                                User.builder()
                                        .kakaoId(kakaoUserInfo.getId())
                                        .email(kakaoUserInfo.getKakaoAccount().getEmail())
                                        .nickname(kakaoUserInfo.getKakaoAccount().getProfile().getNickname())
                                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                                        .build()
                        )
                );
    }
}

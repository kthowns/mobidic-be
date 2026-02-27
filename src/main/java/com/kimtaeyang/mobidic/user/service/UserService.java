package com.kimtaeyang.mobidic.user.service;

import com.kimtaeyang.mobidic.auth.service.AuthService;
import com.kimtaeyang.mobidic.common.code.GeneralResponseCode;
import com.kimtaeyang.mobidic.common.exception.ApiException;
import com.kimtaeyang.mobidic.security.jwt.JwtBlacklistService;
import com.kimtaeyang.mobidic.user.dto.UpdateNicknameRequestDto;
import com.kimtaeyang.mobidic.user.dto.UpdatePasswordRequestDto;
import com.kimtaeyang.mobidic.user.dto.UserDto;
import com.kimtaeyang.mobidic.user.entity.User;
import com.kimtaeyang.mobidic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtBlacklistService jwtBlacklistService;
    private final AuthService authService;

    @Transactional
    public UserDto updateUserNickname(User user, UpdateNicknameRequestDto request) {
        int count = userRepository.countByNicknameAndIdNot(request.getNickname(), user.getId());

        if (count > 0) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_NICKNAME);
        }

        user.setNickname(request.getNickname());
        user = userRepository.save(user);

        return UserDto.fromEntity(user);
    }

    @Transactional
    public UserDto updateUserPassword(User user, UpdatePasswordRequestDto request, String token) {
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        authService.logout(user, token);

        return UserDto.fromEntity(user);
    }

    @Transactional
    public UserDto deactivateUser(User user, String token) {
        user.setDeactivatedAt(LocalDateTime.now());
        user.setIsActive(false);

        jwtBlacklistService.withdrawToken(token);

        return UserDto.fromEntity(user);
    }
}

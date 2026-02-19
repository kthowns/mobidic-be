package com.kimtaeyang.mobidic.user.service;

import com.kimtaeyang.mobidic.auth.service.AuthService;
import com.kimtaeyang.mobidic.common.code.AuthResponseCode;
import com.kimtaeyang.mobidic.common.code.GeneralResponseCode;
import com.kimtaeyang.mobidic.common.exception.ApiException;
import com.kimtaeyang.mobidic.security.JwtBlacklistService;
import com.kimtaeyang.mobidic.user.dto.UpdateNicknameRequestDto;
import com.kimtaeyang.mobidic.user.dto.UpdatePasswordRequestDto;
import com.kimtaeyang.mobidic.user.dto.UserDto;
import com.kimtaeyang.mobidic.user.entity.User;
import com.kimtaeyang.mobidic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtBlacklistService jwtBlacklistService;
    private final AuthService authService;

    @Transactional(readOnly = true)
    @PreAuthorize("@userAccessHandler.ownershipCheck(#userId)")
    public UserDto getUserDetailById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(AuthResponseCode.NO_MEMBER));

        return UserDto.fromEntity(user);
    }

    @Transactional
    @PreAuthorize("@userAccessHandler.ownershipCheck(#userId)")
    public UserDto updateUserNickname(
            UUID userId, UpdateNicknameRequestDto request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(AuthResponseCode.NO_MEMBER));

        int count = userRepository.countByNicknameAndIdNot(request.getNickname(), userId);

        if (count > 0) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_NICKNAME);
        }

        user.setNickname(request.getNickname());
        user = userRepository.save(user);

        return UserDto.fromEntity(user);
    }

    @Transactional
    @PreAuthorize("@userAccessHandler.ownershipCheck(#userId)")
    public UserDto updateUserPassword(
            UUID userId, UpdatePasswordRequestDto request, String token
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(AuthResponseCode.NO_MEMBER));

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        authService.logout(userId, token);

        return UserDto.fromEntity(user);
    }

    @Transactional
    @PreAuthorize("@userAccessHandler.ownershipCheck(#userId)")
    public UserDto deactivateUser(String token, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(AuthResponseCode.NO_MEMBER));

        user.setDeactivatedAt(LocalDateTime.now());
        user.setIsActive(false);
        userRepository.save(user);

        jwtBlacklistService.withdrawToken(token);
        SecurityContextHolder.clearContext();

        return UserDto.fromEntity(user);
    }

    @Transactional
    @PreAuthorize("@userAccessHandler.ownershipCheck(#userId)")
    public UserDto deleteUser(String token, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(AuthResponseCode.NO_MEMBER));
        userRepository.deleteById(userId);

        jwtBlacklistService.withdrawToken(token);
        SecurityContextHolder.clearContext();

        return UserDto.fromEntity(user);
    }
}

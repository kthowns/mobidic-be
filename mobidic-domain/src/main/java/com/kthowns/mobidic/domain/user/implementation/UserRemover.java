package com.kthowns.mobidic.domain.user.implementation;

import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRemover {
    private final UserRepository userRepository;
    private final UserReader userReader;

    public User deactivate(UUID userId) {
        User user = userReader.readById(userId);
        
        User deactivatedUser = User.builder()
                .id(user.getId())
                .kakaoId(user.getKakaoId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .password(user.getPassword())
                .role(user.getRole())
                .isActive(false)
                .createdAt(user.getCreatedAt())
                .deactivatedAt(LocalDateTime.now())
                .build();
        return userRepository.save(deactivatedUser);
    }
}

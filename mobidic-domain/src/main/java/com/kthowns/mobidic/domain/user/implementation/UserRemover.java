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
                .id(user.id())
                .kakaoId(user.kakaoId())
                .email(user.email())
                .nickname(user.nickname())
                .password(user.password())
                .role(user.role())
                .isActive(false)
                .createdAt(user.createdAt())
                .deactivatedAt(LocalDateTime.now())
                .build();
        return userRepository.save(deactivatedUser);
    }
}

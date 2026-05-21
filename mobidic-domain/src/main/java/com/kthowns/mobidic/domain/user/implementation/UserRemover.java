package com.kthowns.mobidic.domain.user.implementation;

import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserRemover {
    private final UserRepository userRepository;

    public User deactivate(User user) {
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

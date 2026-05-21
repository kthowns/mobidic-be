package com.kthowns.mobidic.domain.user.implementation;

import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserUpdater {
    private final UserRepository userRepository;
    private final UserReader userReader;

    public User update(UUID userId, String nickname, String password) {
        User user = userReader.readById(userId);
        
        User updatedUser = User.builder()
                .id(user.getId())
                .kakaoId(user.getKakaoId())
                .email(user.getEmail())
                .nickname(nickname != null ? nickname : user.getNickname())
                .password(password != null ? password : user.getPassword())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
                
        return userRepository.save(updatedUser);
    }
}

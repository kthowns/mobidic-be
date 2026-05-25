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
                .id(user.id())
                .kakaoId(user.kakaoId())
                .email(user.email())
                .nickname(nickname != null ? nickname : user.nickname())
                .password(password != null ? password : user.password())
                .role(user.role())
                .isActive(user.isActive())
                .createdAt(user.createdAt())
                .build();

        return userRepository.update(updatedUser);
    }
}

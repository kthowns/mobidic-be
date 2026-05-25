package com.kthowns.mobidic.domain.user.implementation;

import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.model.UserRole;
import com.kthowns.mobidic.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAppender {
    private final UserRepository userRepository;

    public User append(String email, String nickname, String password, UserRole role) {
        User user = User.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .role(role)
                .isActive(true)
                .build();
        return userRepository.append(user);
    }

    public User appendKakao(Long kakaoId, String email, String nickname, String password) {
        User user = User.builder()
                .kakaoId(kakaoId)
                .email(email)
                .nickname(nickname)
                .password(password)
                .role(UserRole.USER)
                .isActive(true)
                .build();
        return userRepository.append(user);
    }
}

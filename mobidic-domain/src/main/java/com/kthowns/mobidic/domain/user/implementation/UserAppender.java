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
        User user = User.create(email, nickname, password, role);

        return userRepository.append(user);
    }

    public User appendKakao(Long kakaoId, String email, String nickname, String password, UserRole role) {
        User user = User.createKakao(kakaoId, email, nickname, password, role);

        return userRepository.append(user);
    }
}

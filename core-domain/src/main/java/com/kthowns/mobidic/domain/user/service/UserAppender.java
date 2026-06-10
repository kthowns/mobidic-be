package com.kthowns.mobidic.domain.user.service;

import com.kthowns.mobidic.domain.user.client.PasswordEncoderClient;
import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.model.UserRole;
import com.kthowns.mobidic.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserAppender {
    private final UserRepository userRepository;
    private final PasswordEncoderClient passwordEncoderClient;

    public User append(String email, String nickname, String password, UserRole role) {
        String encodedPassword = passwordEncoderClient.encode(password);
        User user = User.create(email, nickname, encodedPassword, role);

        return userRepository.append(user);
    }

    public User appendKakao(Long kakaoId, String email, String nickname, String password, UserRole role) {
        String encodedPassword = passwordEncoderClient.encode(password);
        User user = User.createKakao(kakaoId, email, nickname, encodedPassword, role);

        return userRepository.append(user);
    }
}

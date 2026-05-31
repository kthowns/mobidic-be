package com.kthowns.mobidic.domain.user.service;

import com.kthowns.mobidic.domain.user.client.PasswordEncoderClient;
import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class UserUpdater {
    private final UserRepository userRepository;
    private final UserReader userReader;
    private final PasswordEncoderClient passwordEncoderClient;

    public User update(UUID userId, String nickname, String password) {
        User user = userReader.readById(userId);

        final String encodedPassword = (password != null && !password.isBlank())
                ? passwordEncoderClient.encode(password)
                : null;

        return userRepository.update(user.updateProfile(nickname, encodedPassword));
    }
}

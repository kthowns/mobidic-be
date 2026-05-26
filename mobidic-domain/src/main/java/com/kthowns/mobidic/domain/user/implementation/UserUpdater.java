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

        return userRepository.update(
                user.changeNickname(nickname)
                        .changePassword(password)
        );
    }
}

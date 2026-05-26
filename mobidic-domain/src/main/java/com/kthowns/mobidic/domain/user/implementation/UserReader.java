package com.kthowns.mobidic.domain.user.implementation;

import com.kthowns.mobidic.common.code.AuthResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserReader {
    private final UserRepository userRepository;

    public User readById(UUID id) {
        return userRepository.readById(id)
                .orElseThrow(() -> new ApiException(AuthResponseCode.NO_USER));
    }
}

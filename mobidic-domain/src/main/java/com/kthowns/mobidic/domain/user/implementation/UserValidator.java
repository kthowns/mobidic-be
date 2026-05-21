package com.kthowns.mobidic.domain.user.implementation;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void validateEmailDuplication(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_EMAIL);
        }
    }

    public void validateNicknameDuplication(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_NICKNAME);
        }
    }

    public void validateNicknameUpdateDuplication(String nickname, UUID userId) {
        if (userRepository.existsByNicknameAndIdNot(nickname, userId)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_NICKNAME);
        }
    }
}

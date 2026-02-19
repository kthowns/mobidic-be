package com.kimtaeyang.mobidic.security.accesshandler;

import com.kimtaeyang.mobidic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAccessHandler extends AccessHandler {
    private final UserRepository userRepository;

    @Override
    boolean isResourceOwner(UUID resourceId) {
        return userRepository.findById(resourceId)
                .filter((m) -> getCurrentMemberId().equals(resourceId))
                .isPresent();
    }
}

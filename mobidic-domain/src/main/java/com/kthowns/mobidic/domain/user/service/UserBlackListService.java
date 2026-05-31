package com.kthowns.mobidic.domain.user.service;

import com.kthowns.mobidic.domain.auth.repository.BlackListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserBlackListService {
    private final BlackListRepository blackListRepository;

    public void registerDeactivatedUser(UUID userId, long ttlMillis) {
        blackListRepository.saveDeactivated(userId, ttlMillis);
    }

    public boolean isDeactivatedUser(UUID userId) {
        return blackListRepository.existsDeactivated(userId);
    }
}

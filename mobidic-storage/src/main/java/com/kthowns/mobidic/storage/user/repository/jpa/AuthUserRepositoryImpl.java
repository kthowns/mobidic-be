package com.kthowns.mobidic.storage.user.repository.jpa;


import com.kthowns.mobidic.storage.user.jpaentity.UserJpaEntity;
import com.kthowns.mobidic.storage.user.jparepository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthUserRepositoryImpl {
    private final UserJpaRepository userJpaRepository;

    public Optional<UserJpaEntity> readByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    public Optional<UserJpaEntity> readByKakaoId(Long kakaoId) {
        return userJpaRepository.findByKakaoId(kakaoId);
    }
}

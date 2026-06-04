package com.kthowns.mobidic.storage.user.repository.jpa;


import com.kthowns.mobidic.domain.auth.repository.AuthUserRepository;
import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.storage.user.jpaentity.UserJpaEntity;
import com.kthowns.mobidic.storage.user.jparepository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthUserRepositoryImpl implements AuthUserRepository {
    private final UserJpaRepository userJpaRepository;

    public Optional<User> readByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(UserJpaEntity::toModel);
    }

    public Optional<User> readByKakaoId(Long kakaoId) {
        return userJpaRepository.findByKakaoId(kakaoId).map(UserJpaEntity::toModel);
    }
}

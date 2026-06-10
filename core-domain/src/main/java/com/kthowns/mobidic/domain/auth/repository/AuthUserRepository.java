package com.kthowns.mobidic.domain.auth.repository;

import com.kthowns.mobidic.domain.user.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthUserRepository {
    Optional<User> readByEmail(String email);

    Optional<User> readByKakaoId(Long kakaoId);

}

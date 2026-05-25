package com.kthowns.mobidic.domain.user.repository;

import com.kthowns.mobidic.domain.user.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User append(User user);

    User update(User user);

    Optional<User> readById(UUID id);

    Optional<User> readByEmail(String email);

    Optional<User> readByKakaoId(Long kakaoId);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByNicknameAndIdNot(String nickname, UUID id);
}

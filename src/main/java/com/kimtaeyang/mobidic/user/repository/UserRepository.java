package com.kimtaeyang.mobidic.user.repository;

import com.kimtaeyang.mobidic.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    int countByNickname(String nickname);

    int countByEmail(String email);

    int countByNicknameAndIdNot(String nickname, UUID id);
}
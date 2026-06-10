package com.kthowns.mobidic.storage.user.jparepository;

import com.kthowns.mobidic.storage.user.jpaentity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    boolean existsByNicknameAndIdNot(String nickname, UUID id);

    Optional<UserJpaEntity> findByKakaoId(Long kakaoId);

    boolean existsByIdAndActiveFalse(UUID id);
}
package com.kthowns.mobidic.storage.user.repository.jpa;

import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.repository.UserRepository;
import com.kthowns.mobidic.storage.user.jpaentity.UserJpaEntity;
import com.kthowns.mobidic.storage.user.jparepository.UserJpaEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaEntityRepository userJpaEntityRepository;

    @Override
    public User append(User user) {
        UserJpaEntity userJpaEntity = UserJpaEntity.builder()
                .email(user.email())
                .password(user.password())
                .kakaoId(user.kakaoId())
                .role(user.role())
                .isActive(user.isActive())
                .build();

        return userJpaEntityRepository.save(userJpaEntity).toModel();
    }

    @Override
    public User update(User user) {
        return null;
    }


    @Override
    public Optional<User> readById(UUID id) {
        return userJpaEntityRepository.findById(id).map(UserJpaEntity::toModel);
    }

    @Override
    public Optional<User> readByEmail(String email) {
        return userJpaEntityRepository.findByEmail(email).map(UserJpaEntity::toModel);
    }

    @Override
    public Optional<User> readByKakaoId(Long kakaoId) {
        return userJpaEntityRepository.findByKakaoId(kakaoId).map(UserJpaEntity::toModel);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaEntityRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return userJpaEntityRepository.existsByNickname(nickname);
    }

    @Override
    public boolean existsByNicknameAndIdNot(String nickname, UUID id) {
        return userJpaEntityRepository.existsByNicknameAndIdNot(nickname, id);
    }

    private void updateEntityFromModel(UserJpaEntity entity, User model) {
        entity.update(
                model.nickname(),
                model.password(),
                model.role(),
                model.isActive(),
                model.deactivatedAt(),
                model.kakaoId()
        );
    }
}

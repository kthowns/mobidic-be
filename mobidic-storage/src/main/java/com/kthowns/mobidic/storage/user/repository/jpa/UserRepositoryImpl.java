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
    public User save(User user) {
        UserJpaEntity entity = userJpaEntityRepository.findById(user.getId() != null ? user.getId() : UUID.randomUUID())
                .orElseGet(() -> UserJpaEntity.builder()
                        .email(user.getEmail())
                        .build());
        
        updateEntityFromModel(entity, user);
        UserJpaEntity savedEntity = userJpaEntityRepository.save(entity);
        return mapToModel(savedEntity);
    }

    @Override
    public Optional<User> readById(UUID id) {
        return userJpaEntityRepository.findById(id).map(this::mapToModel);
    }

    @Override
    public Optional<User> readByEmail(String email) {
        return userJpaEntityRepository.findByEmail(email).map(this::mapToModel);
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

    private User mapToModel(UserJpaEntity entity) {
        return User.builder()
                .id(entity.getId())
                .kakaoId(entity.getKakaoId())
                .email(entity.getEmail())
                .nickname(entity.getNickname())
                .password(entity.getPassword())
                .role(entity.getRole())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .deactivatedAt(entity.getDeactivatedAt())
                .build();
    }

    private void updateEntityFromModel(UserJpaEntity entity, User model) {
        entity.setNickname(model.getNickname());
        entity.setPassword(model.getPassword());
        entity.setRole(model.getRole());
        entity.setIsActive(model.getIsActive());
        entity.setDeactivatedAt(model.getDeactivatedAt());
        if (model.getKakaoId() != null) {
            entity.setKakaoId(model.getKakaoId());
        }
    }
}

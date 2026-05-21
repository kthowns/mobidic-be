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
        return userJpaEntityRepository.save(entity).toModel();
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
                model.getNickname(),
                model.getPassword(),
                model.getRole(),
                model.getIsActive(),
                model.getDeactivatedAt(),
                model.getKakaoId()
        );
    }
}

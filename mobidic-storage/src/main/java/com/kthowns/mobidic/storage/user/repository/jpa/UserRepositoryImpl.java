package com.kthowns.mobidic.storage.user.repository.jpa;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.repository.UserRepository;
import com.kthowns.mobidic.storage.user.jpaentity.UserJpaEntity;
import com.kthowns.mobidic.storage.user.jparepository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public User append(User user) {
        UserJpaEntity userJpaEntity = UserJpaEntity.createFromModel(user);

        return userJpaRepository.save(userJpaEntity).toModel();
    }

    @Override
    public User update(User user) {
        UserJpaEntity userJpaEntity = userJpaRepository.findById(user.id())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.INVALID_REQUEST));

        userJpaEntity.updateFromModel(user);

        return userJpaEntity.toModel();
    }

    @Override
    public Optional<User> readById(UUID id) {
        return userJpaRepository.findById(id).map(UserJpaEntity::toModel);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return userJpaRepository.existsByNickname(nickname);
    }

    @Override
    public boolean existsByNicknameAndIdNot(String nickname, UUID id) {
        return userJpaRepository.existsByNicknameAndIdNot(nickname, id);
    }
}

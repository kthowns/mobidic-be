package com.kimtaeyang.mobidic.dictionary.repository;

import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
import com.kimtaeyang.mobidic.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, UUID>, VocabularyDetailRepositoryCustom {
    boolean existsByTitleAndUserAndIdNot(String title, User user, UUID id);

    boolean existsByTitleAndUser(String title, User user);

    Optional<Vocabulary> findByIdAndUser_Id(UUID id, UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from Vocabulary v where v.user.id = :userId and v.id = :id")
    Optional<Vocabulary> findForUpdate(@Param("id") UUID id, @Param("userId") UUID userId);

    boolean existsByIdAndUser_Id(UUID id, UUID userId);

    boolean existsByUser(User user);
}
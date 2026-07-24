package com.kthowns.mobidic.storage.vocabulary.jparepository;

import com.kthowns.mobidic.storage.vocabulary.jpaentity.VocabularyJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VocabularyJpaRepository extends JpaRepository<VocabularyJpaEntity, UUID>, VocabularyDetailJpaRepositoryCustom {
    Optional<VocabularyJpaEntity> findByIdAndUserId(UUID id, UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM VocabularyJpaEntity v" +
            " WHERE v.userId = :userId" +
            " AND v.id = :id")
    Optional<VocabularyJpaEntity> findForUpdate(
            @Param("id") UUID id,
            @Param("userId") UUID userId
    );

    boolean existsByIdAndUserId(UUID id, UUID userId);

    boolean existsByTitleAndUserId(String title, UUID userId);

    boolean existsByTitleAndIdNotAndUserId(String title, UUID vocabularyId, UUID userId);

    boolean existsByUserId(UUID userId);

    @Modifying
    @Query("UPDATE VocabularyJpaEntity v" +
            " SET v.wordCount = v.wordCount + 1" +
            " WHERE v.id = :vocabularyId AND v.userId = :userId")
    void increaseWordCount(
            @Param("vocabularyId") UUID vocabularyId,
            @Param("userId") UUID userId
    );

    @Modifying
    @Query("UPDATE VocabularyJpaEntity v" +
            " SET v.wordCount = v.wordCount - 1" +
            " WHERE v.id = :vocabularyId and v.userId = :userId and v.wordCount > 0")
    void decreaseWordCount(
            @Param("vocabularyId") UUID vocabularyId,
            @Param("userId") UUID userId
    );
}

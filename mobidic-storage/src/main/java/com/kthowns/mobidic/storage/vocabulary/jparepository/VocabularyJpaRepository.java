package com.kthowns.mobidic.storage.vocabulary.jparepository;

import com.kthowns.mobidic.storage.vocabulary.jpaentity.VocabularyJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VocabularyJpaRepository extends JpaRepository<VocabularyJpaEntity, UUID>, VocabularyDetailJpaRepositoryCustom {
    Optional<VocabularyJpaEntity> findByIdAndUser_Id(UUID id, UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from VocabularyJpaEntity v where v.user.id = :userId and v.id = :id")
    Optional<VocabularyJpaEntity> findForUpdate(@Param("id") UUID id, @Param("userId") UUID userId);

    boolean existsByIdAndUser_Id(UUID id, UUID userId);
}
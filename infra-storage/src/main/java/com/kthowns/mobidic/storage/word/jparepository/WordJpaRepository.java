package com.kthowns.mobidic.storage.word.jparepository;

import com.kthowns.mobidic.storage.word.jpaentity.WordJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WordJpaRepository extends JpaRepository<WordJpaEntity, UUID>, WordJpaRepositoryCustom {
    Optional<WordJpaEntity> findByIdAndVocabulary_UserId(UUID id, UUID userId);

    boolean existsByExpressionAndVocabulary_IdAndVocabulary_UserId(String expression, UUID vocabularyId, UUID userId);

    boolean existsByExpressionAndVocabulary_IdAndIdNotAndVocabulary_UserId(String expression, UUID vocabularyId, UUID wordId, UUID userId);
}

package com.kthowns.mobidic.storage.word.jparepository;

import com.kthowns.mobidic.storage.word.jpaentity.WordJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WordJpaRepository extends JpaRepository<WordJpaEntity, UUID>, WordJpaRepositoryCustom {
    Optional<WordJpaEntity> findByIdAndVocabulary_User_Id(UUID id, UUID userId);

    boolean existsByExpressionAndVocabulary_Id(String expression, UUID vocabularyId);

    boolean existsByExpressionAndVocabulary_IdAndIdNot(String expression, UUID vocabularyId, UUID wordId);
}

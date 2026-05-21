package com.kthowns.mobidic.storage.dictionary.jparepository;

import com.kthowns.mobidic.storage.dictionary.jpaentity.VocabularyJpaEntity;
import com.kthowns.mobidic.storage.dictionary.jpaentity.WordJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WordJpaRepository extends JpaRepository<WordJpaEntity, UUID>, WordJpaRepositoryCustom {
    long countByVocabulary(VocabularyJpaEntity vocabulary);

    Optional<WordJpaEntity> findByIdAndVocabulary_User_Id(UUID id, UUID userId);

    boolean existsByExpressionAndVocabulary(String expression, VocabularyJpaEntity vocabulary);
}

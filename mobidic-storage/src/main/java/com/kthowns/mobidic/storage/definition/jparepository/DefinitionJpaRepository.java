package com.kthowns.mobidic.storage.definition.jparepository;

import com.kthowns.mobidic.storage.definition.jpaentity.DefinitionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DefinitionJpaRepository extends JpaRepository<DefinitionJpaEntity, UUID> {
    Optional<DefinitionJpaEntity> findByIdAndWord_Vocabulary_User_Id(UUID id, UUID wordVocabularyUserId);

    List<DefinitionJpaEntity> findByWord_IdAndWord_Vocabulary_User_Id(UUID wordId, UUID userId);

    boolean existsByMeaningAndWord_IdAndWord_Vocabulary_User_Id(String meaning, UUID wordId, UUID userId);

    boolean existsByMeaningAndWord_IdAndIdNotAndWord_Vocabulary_User_Id(String meaning, UUID wordId, UUID id, UUID userId);
}

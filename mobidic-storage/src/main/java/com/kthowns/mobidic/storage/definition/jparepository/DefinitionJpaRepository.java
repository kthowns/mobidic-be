package com.kthowns.mobidic.storage.definition.jparepository;

import com.kthowns.mobidic.storage.definition.jpaentity.DefinitionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DefinitionJpaRepository extends JpaRepository<DefinitionJpaEntity, UUID> {
    Optional<DefinitionJpaEntity> findByIdAndWord_Vocabulary_User_Id(UUID id, UUID wordVocabularyUserId);

    List<DefinitionJpaEntity> findByWord_IdAndWord_Vocabulary_User_Id(UUID wordId, UUID userId);

    boolean existsByMeaningInAndWord_IdAndWord_Vocabulary_User_Id(Collection<String> meanings, UUID wordId, UUID wordVocabularyUserId);

    boolean existsByMeaningInAndIdNotInAndWord_IdAndWord_Vocabulary_User_Id(List<String> meanings, List<UUID> ids, UUID wordId, UUID userId);

    List<DefinitionJpaEntity> findByIdInAndWord_IdAndWord_Vocabulary_User_Id(Collection<UUID> ids, UUID wordId, UUID wordVocabularyUserId);
}

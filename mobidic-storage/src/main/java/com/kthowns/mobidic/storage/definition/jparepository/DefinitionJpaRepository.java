package com.kthowns.mobidic.storage.definition.jparepository;

import com.kthowns.mobidic.storage.definition.jpaentity.DefinitionJpaEntity;
import com.kthowns.mobidic.storage.word.jpaentity.WordJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DefinitionJpaRepository extends JpaRepository<DefinitionJpaEntity, UUID> {
    List<DefinitionJpaEntity> findByWord(WordJpaEntity word);

    boolean existsByMeaningAndWord(String definition, WordJpaEntity word);

    boolean existsByMeaningAndWordAndIdNot(String definition, WordJpaEntity word, UUID id);

    Optional<DefinitionJpaEntity> findByIdAndWord_Vocabulary_User_Id(UUID id, UUID wordVocabularyUserId);
}

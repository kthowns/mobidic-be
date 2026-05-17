package com.kthowns.mobidic.storage.dictionary.jparepository;

import com.kthowns.mobidic.storage.dictionary.jpaentity.Definition;
import com.kthowns.mobidic.storage.dictionary.jpaentity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DefinitionRepository extends JpaRepository<Definition, UUID> {
    List<Definition> findByWord(Word word);

    boolean existsByMeaningAndWord(String definition, Word word);

    boolean existsByMeaningAndWordAndIdNot(String definition, Word word, UUID id);

    Optional<Definition> findByIdAndWord_Vocabulary_User_Id(UUID id, UUID wordVocabularyUserId);
}

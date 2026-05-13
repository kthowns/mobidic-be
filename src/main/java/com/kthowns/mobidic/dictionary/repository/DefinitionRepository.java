package com.kthowns.mobidic.dictionary.repository;

import com.kthowns.mobidic.dictionary.entity.Definition;
import com.kthowns.mobidic.dictionary.entity.Word;
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

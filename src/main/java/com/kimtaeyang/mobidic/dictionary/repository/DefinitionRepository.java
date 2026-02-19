package com.kimtaeyang.mobidic.dictionary.repository;

import com.kimtaeyang.mobidic.dictionary.entity.Definition;
import com.kimtaeyang.mobidic.dictionary.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DefinitionRepository extends JpaRepository<Definition, UUID> {
    List<Definition> findByWord(Word word);

    int countByDefinitionAndWord(String definition, Word word);

    int countByDefinitionAndWordAndIdNot(String definition, Word word, UUID id);
}

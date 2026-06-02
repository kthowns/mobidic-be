package com.kthowns.mobidic.domain.definition.repository;

import com.kthowns.mobidic.domain.definition.model.Definition;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DefinitionRepository {
    void append(Definition definition);

    void appendAll(List<Definition> definitions);

    Optional<Definition> readByIdAndUserId(UUID definitionId, UUID userId);

    List<Definition> readByWordId(UUID wordId, UUID userId);

    void update(Definition definition, UUID userId);

    void delete(UUID definitionId, UUID userId);

    boolean existsByMeaningAndWordId(String meaning, UUID wordId, UUID userId);

    boolean existsByMeaningAndWordIdAndIdNot(String meaning, UUID wordId, UUID definitionId, UUID userId);
}

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

    void update(Definition definition, UUID wordId, UUID userId);

    void updateAll(List<Definition> definitions, UUID wordId, UUID userId);

    boolean existsByMeaningsForAppend(List<String> addingDefinitionMeanings, UUID wordId, UUID userId);

    boolean existsByMeaningsForUpdate(List<String> meanings, List<UUID> ids, UUID wordId, UUID userId);

    List<Definition> readByIdsAndWordIdAndUserId(List<UUID> definitionIds, UUID wordId, UUID userId);

    void deleteAll(List<UUID> ids, UUID wordId, UUID userId);
}

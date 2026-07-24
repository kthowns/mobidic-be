package com.kthowns.mobidic.storage.definition.repository.jpa;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import com.kthowns.mobidic.storage.definition.jpaentity.DefinitionJpaEntity;
import com.kthowns.mobidic.storage.definition.jparepository.DefinitionJpaRepository;
import com.kthowns.mobidic.storage.word.jpaentity.WordJpaEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DefinitionRepositoryImpl implements DefinitionRepository {
    private final DefinitionJpaRepository definitionJpaRepository;
    private final EntityManager em;

    @Override
    public void append(Definition definition) {
        WordJpaEntity word = em.getReference(WordJpaEntity.class, definition.wordId());

        DefinitionJpaEntity definitionJpaEntity = DefinitionJpaEntity.createFromModel(definition, word);
        definitionJpaRepository.save(definitionJpaEntity);
    }

    @Override
    public void appendAll(List<Definition> definitions) {
        if (definitions == null || definitions.isEmpty()) {
            return;
        }

        List<DefinitionJpaEntity> entities = definitions.stream()
                .map(def -> {
                    WordJpaEntity word = em.getReference(WordJpaEntity.class, def.wordId());
                    return DefinitionJpaEntity.createFromModel(def, word);
                })
                .toList();

        definitionJpaRepository.saveAll(entities);
    }

    @Override
    public Optional<Definition> readByIdAndUserId(UUID definitionId, UUID userId) {
        return definitionJpaRepository.findByIdAndWord_Vocabulary_UserId(definitionId, userId)
                .map(DefinitionJpaEntity::toModel);
    }

    @Override
    public List<Definition> readByWordId(UUID wordId, UUID userId) {
        return definitionJpaRepository.findByWord_IdAndWord_Vocabulary_UserId(wordId, userId).stream()
                .map(DefinitionJpaEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void update(Definition definition, UUID wordId, UUID userId) {
        DefinitionJpaEntity definitionJpaEntity = em.find(DefinitionJpaEntity.class, definition.id());
        if (definitionJpaEntity == null) throw new ApiException(GeneralResponseCode.NO_DEF);
        definitionJpaEntity.updateFromModel(definition);
    }

    @Override
    public void updateAll(List<Definition> definitions, UUID wordId, UUID userId) {
        List<UUID> ids = definitions.stream().map(Definition::id).toList();

        Map<UUID, Definition> modelMap = definitions.stream()
                .collect(Collectors.toMap(Definition::id, d -> d));

        for (UUID id : ids) {
            DefinitionJpaEntity definitionJpaEntity = em.find(DefinitionJpaEntity.class, id);
            if (definitionJpaEntity == null) {
                throw new ApiException(GeneralResponseCode.NO_DEF);
            }
            if (!definitionJpaEntity.getWord().getId().equals(wordId)) {
                throw new ApiException(GeneralResponseCode.INVALID_REQUEST_BODY);
            }
            definitionJpaEntity.updateFromModel(modelMap.get(id));
        }
    }

    @Override
    public boolean existsByMeaningsForAppend(List<String> meanings, UUID wordId, UUID userId) {
        return definitionJpaRepository.existsByMeaningInAndWord_IdAndWord_Vocabulary_UserId(meanings, wordId, userId);
    }

    @Override
    public boolean existsByMeaningsForUpdate(List<String> meanings, List<UUID> ids, UUID wordId, UUID userId) {
        return definitionJpaRepository.existsByMeaningInAndIdNotInAndWord_IdAndWord_Vocabulary_UserId(
                meanings, ids, wordId, userId
        );
    }

    @Override
    public List<Definition> readByIdsAndWordIdAndUserId(List<UUID> definitionIds, UUID wordId, UUID userId) {
        return definitionJpaRepository.findByIdInAndWord_IdAndWord_Vocabulary_UserId(definitionIds, wordId, userId)
                .stream().map(DefinitionJpaEntity::toModel).toList();
    }

    @Override
    public void deleteAll(List<UUID> ids, UUID wordId, UUID userId) {
        List<DefinitionJpaEntity> definitions = definitionJpaRepository.findByIdInAndWord_IdAndWord_Vocabulary_UserId(ids, wordId, userId);

        if (ids.size() != definitions.size()) {
            throw new ApiException(GeneralResponseCode.INVALID_REQUEST_BODY);
        }

        definitionJpaRepository.deleteAllInBatch(definitions);
        em.flush();
    }
}

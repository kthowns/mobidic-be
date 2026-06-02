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
        return definitionJpaRepository.findByIdAndWord_Vocabulary_User_Id(definitionId, userId)
                .map(DefinitionJpaEntity::toModel);
    }

    @Override
    public List<Definition> readByWordId(UUID wordId, UUID userId) {
        return definitionJpaRepository.findByWord_IdAndWord_Vocabulary_User_Id(wordId, userId).stream()
                .map(DefinitionJpaEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void update(Definition definition, UUID userId) {
        DefinitionJpaEntity definitionJpaEntity = definitionJpaRepository.findByIdAndWord_Vocabulary_User_Id(definition.id(), userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_DEF));

        definitionJpaEntity.updateFromModel(definition);
    }

    @Override
    public void delete(UUID definitionId, UUID userId) {
        DefinitionJpaEntity definitionJpaEntity = definitionJpaRepository.findByIdAndWord_Vocabulary_User_Id(definitionId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_DEF));
        definitionJpaRepository.delete(definitionJpaEntity);
    }

    @Override
    public boolean existsByMeaningAndWordId(String meaning, UUID wordId, UUID userId) {
        return definitionJpaRepository.existsByMeaningAndWord_IdAndWord_Vocabulary_User_Id(meaning, wordId, userId);
    }

    @Override
    public boolean existsByMeaningAndWordIdAndIdNot(String meaning, UUID wordId, UUID definitionId, UUID userId) {
        return definitionJpaRepository.existsByMeaningAndWord_IdAndIdNotAndWord_Vocabulary_User_Id(meaning, wordId, definitionId, userId);
    }
}

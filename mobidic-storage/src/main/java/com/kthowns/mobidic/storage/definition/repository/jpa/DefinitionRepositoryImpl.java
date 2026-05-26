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
    public Optional<Definition> readByIdAndUserId(UUID definitionId, UUID userId) {
        return definitionJpaRepository.findByIdAndWord_Vocabulary_User_Id(definitionId, userId)
                .map(DefinitionJpaEntity::toModel);
    }

    @Override
    public List<Definition> readByWordId(UUID wordId) {
        return definitionJpaRepository.findByWord_Id(wordId).stream()
                .map(DefinitionJpaEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void update(Definition definition) {
        DefinitionJpaEntity definitionJpaEntity = definitionJpaRepository.findById(definition.id())
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
    public boolean existsByMeaningAndWordId(String meaning, UUID wordId) {
        return definitionJpaRepository.existsByMeaningAndWord_Id(meaning, wordId);
    }

    @Override
    public boolean existsByMeaningAndWordIdAndIdNot(String meaning, UUID wordId, UUID definitionId) {
        return definitionJpaRepository.existsByMeaningAndWord_IdAndIdNot(meaning, wordId, definitionId);
    }
}

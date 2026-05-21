package com.kthowns.mobidic.storage.definition.repository.jpa;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import com.kthowns.mobidic.storage.definition.jpaentity.DefinitionJpaEntity;
import com.kthowns.mobidic.storage.definition.jparepository.DefinitionJpaRepository;
import com.kthowns.mobidic.storage.word.jpaentity.WordJpaEntity;
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

    @Override
    public void append(Definition definition) {
        DefinitionJpaEntity definitionJpaEntity = DefinitionJpaEntity.builder()
                .meaning(definition.getMeaning())
                .part(definition.getPart())
                .word(WordJpaEntity.builder().id(definition.getWordId()).build())
                .build();
        definitionJpaRepository.save(definitionJpaEntity);
    }

    @Override
    public Optional<Definition> readByIdAndUserId(UUID definitionId, UUID userId) {
        return definitionJpaRepository.findByIdAndWord_Vocabulary_User_Id(definitionId, userId)
                .map(entity -> Definition.builder()
                        .id(entity.getId())
                        .wordId(entity.getWord().getId())
                        .meaning(entity.getMeaning())
                        .part(entity.getPart())
                        .build());
    }

    @Override
    public List<Definition> readByWordId(UUID wordId) {
        return definitionJpaRepository.findByWord_Id(wordId).stream()
                .map(entity -> Definition.builder()
                        .id(entity.getId())
                        .wordId(entity.getWord().getId())
                        .meaning(entity.getMeaning())
                        .part(entity.getPart())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void update(Definition definition) {
        DefinitionJpaEntity definitionJpaEntity = definitionJpaRepository.findById(definition.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_DEF));
        
        definitionJpaEntity.setMeaning(definition.getMeaning());
        definitionJpaEntity.setPart(definition.getPart());
        definitionJpaRepository.save(definitionJpaEntity);
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

package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.word.model.Word;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import com.kthowns.mobidic.domain.word.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefinitionService {
    private final WordRepository wordRepository;
    private final DefinitionRepository definitionRepository;

    @Transactional
    public Definition addDefinition(
            UUID userId,
            UUID wordId,
            AddDefinitionRequestDto request
    ) {
        Word word = wordRepository.findByIdAndVocabulary_User_Id(wordId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));

        if (definitionRepository.existsByMeaningAndWord(request.getMeaning(), word)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_DEFINITION);
        }

        Definition definition = Definition.builder()
                .word(word)
                .part(request.getPart())
                .meaning(request.getMeaning())
                .build();
        definitionRepository.save(definition);

        return Definition.fromEntity(definition);
    }

    @Transactional(readOnly = true)
    public List<Definition> getDefinitionsByWordId(User user, UUID wordId) {
        Word word = wordRepository.findByIdAndVocabulary_User_Id(wordId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));

        return definitionRepository.findByWord(word)
                .stream().map(Definition::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public Definition updateDefinition(
            User user,
            UUID defId,
            AddDefinitionRequestDto request
    ) {
        Definition definition = definitionRepository.findByIdAndWord_Vocabulary_User_Id(
                defId, user.getId()
        ).orElseThrow(() -> new ApiException(GeneralResponseCode.NO_DEF));

        if (definitionRepository.existsByMeaningAndWordAndIdNot(request.getMeaning(), definition.getWord(), defId)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_DEFINITION);
        }

        definition.setMeaning(request.getMeaning());
        definition.setPart(request.getPart());
        definitionRepository.save(definition);

        return Definition.fromEntity(definition);
    }

    @Transactional
    public Definition deleteDefinition(
            User user,
            UUID defId
    ) {
        Definition definition = definitionRepository.findByIdAndWord_Vocabulary_User_Id(
                defId, user.getId()
        ).orElseThrow(() -> new ApiException(GeneralResponseCode.NO_DEF));

        definitionRepository.delete(definition);

        return Definition.fromEntity(definition);
    }
}

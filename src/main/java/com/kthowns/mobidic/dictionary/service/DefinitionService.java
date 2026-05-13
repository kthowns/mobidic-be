package com.kthowns.mobidic.dictionary.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.dictionary.dto.AddDefinitionRequestDto;
import com.kthowns.mobidic.dictionary.dto.DefinitionDto;
import com.kthowns.mobidic.dictionary.entity.Definition;
import com.kthowns.mobidic.dictionary.entity.Word;
import com.kthowns.mobidic.dictionary.repository.DefinitionRepository;
import com.kthowns.mobidic.dictionary.repository.WordRepository;
import com.kthowns.mobidic.user.entity.User;
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
    public DefinitionDto addDefinition(
            User user,
            UUID wordId,
            AddDefinitionRequestDto request
    ) {
        Word word = wordRepository.findByIdAndVocabulary_User_Id(wordId, user.getId())
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

        return DefinitionDto.fromEntity(definition);
    }

    @Transactional(readOnly = true)
    public List<DefinitionDto> getDefinitionsByWordId(User user, UUID wordId) {
        Word word = wordRepository.findByIdAndVocabulary_User_Id(wordId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));

        return definitionRepository.findByWord(word)
                .stream().map(DefinitionDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public DefinitionDto updateDefinition(
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

        return DefinitionDto.fromEntity(definition);
    }

    @Transactional
    public DefinitionDto deleteDefinition(
            User user,
            UUID defId
    ) {
        Definition definition = definitionRepository.findByIdAndWord_Vocabulary_User_Id(
                defId, user.getId()
        ).orElseThrow(() -> new ApiException(GeneralResponseCode.NO_DEF));

        definitionRepository.delete(definition);

        return DefinitionDto.fromEntity(definition);
    }
}

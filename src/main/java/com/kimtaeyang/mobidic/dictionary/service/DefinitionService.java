package com.kimtaeyang.mobidic.dictionary.service;

import com.kimtaeyang.mobidic.common.code.GeneralResponseCode;
import com.kimtaeyang.mobidic.common.exception.ApiException;
import com.kimtaeyang.mobidic.dictionary.dto.AddDefinitionRequestDto;
import com.kimtaeyang.mobidic.dictionary.dto.DefinitionDto;
import com.kimtaeyang.mobidic.dictionary.entity.Definition;
import com.kimtaeyang.mobidic.dictionary.entity.Word;
import com.kimtaeyang.mobidic.dictionary.repository.DefinitionRepository;
import com.kimtaeyang.mobidic.dictionary.repository.WordRepository;
import com.kimtaeyang.mobidic.user.entity.User;
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

        int count = definitionRepository.countByDefinitionAndWord(request.getDefinition(), word);

        if (count > 0) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_DEFINITION);
        }

        Definition definition = Definition.builder()
                .word(word)
                .part(request.getPart())
                .definition(request.getDefinition())
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

        int count = definitionRepository.countByDefinitionAndWordAndIdNot(request.getDefinition(), definition.getWord(), defId);

        if (count > 0) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_DEFINITION);
        }

        definition.setDefinition(request.getDefinition());
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

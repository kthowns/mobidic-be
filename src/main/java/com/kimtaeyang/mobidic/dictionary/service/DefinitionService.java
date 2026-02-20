package com.kimtaeyang.mobidic.dictionary.service;

import com.kimtaeyang.mobidic.common.code.GeneralResponseCode;
import com.kimtaeyang.mobidic.dictionary.dto.AddDefinitionRequestDto;
import com.kimtaeyang.mobidic.dictionary.dto.DefinitionDto;
import com.kimtaeyang.mobidic.dictionary.entity.Definition;
import com.kimtaeyang.mobidic.dictionary.entity.Word;
import com.kimtaeyang.mobidic.common.exception.ApiException;
import com.kimtaeyang.mobidic.dictionary.repository.DefinitionRepository;
import com.kimtaeyang.mobidic.dictionary.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("@wordAccessHandler.ownershipCheck(#wordId)")
    public DefinitionDto addDefinition(UUID wordId, AddDefinitionRequestDto request) {
        Word word = wordRepository.findById(wordId)
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

    @PreAuthorize("@wordAccessHandler.ownershipCheck(#wordId)")
    @Transactional(readOnly = true)
    public List<DefinitionDto> getDefinitionsByWordId(UUID wordId) {
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));

        return definitionRepository.findByWord(word)
                .stream().map(DefinitionDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    @PreAuthorize("@definitionAccessHandler.ownershipCheck(#defId)")
    public DefinitionDto updateDefinition(UUID defId, AddDefinitionRequestDto request) {
        Definition definition = definitionRepository.findById(defId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_DEF));

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
    @PreAuthorize("@definitionAccessHandler.ownershipCheck(#defId)")
    public DefinitionDto deleteDefinition(UUID defId) {
        Definition definition = definitionRepository.findById(defId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_DEF));

        definitionRepository.delete(definition);

        return DefinitionDto.fromEntity(definition);
    }
}

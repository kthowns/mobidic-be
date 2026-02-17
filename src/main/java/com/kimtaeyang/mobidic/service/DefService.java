package com.kimtaeyang.mobidic.service;

import com.kimtaeyang.mobidic.dto.AddDefRequestDto;
import com.kimtaeyang.mobidic.dto.DefDto;
import com.kimtaeyang.mobidic.entity.Def;
import com.kimtaeyang.mobidic.entity.Word;
import com.kimtaeyang.mobidic.exception.ApiException;
import com.kimtaeyang.mobidic.repository.DefRepository;
import com.kimtaeyang.mobidic.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.kimtaeyang.mobidic.code.GeneralResponseCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefService {
    private final WordRepository wordRepository;
    private final DefRepository defRepository;

    @Transactional
    @PreAuthorize("@wordAccessHandler.ownershipCheck(#wordId)")
    public DefDto addDef(UUID wordId, AddDefRequestDto request) {
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new ApiException(NO_WORD));

        int count = defRepository.countByDefinitionAndWord(request.getDefinition(), word);

        if (count > 0) {
            throw new ApiException(DUPLICATED_DEFINITION);
        }

        Def def = Def.builder()
                .word(word)
                .part(request.getPart())
                .definition(request.getDefinition())
                .build();
        defRepository.save(def);

        return DefDto.fromEntity(def);
    }

    @PreAuthorize("@wordAccessHandler.ownershipCheck(#wordId)")
    @Transactional(readOnly = true)
    public List<DefDto> getDefsByWordId(UUID wordId) {
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new ApiException(NO_WORD));

        return defRepository.findByWord(word)
                .stream().map(DefDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    @PreAuthorize("@defAccessHandler.ownershipCheck(#defId)")
    public DefDto updateDef(UUID defId, AddDefRequestDto request) {
        Def def = defRepository.findById(defId)
                .orElseThrow(() -> new ApiException(NO_DEF));

        int count = defRepository.countByDefinitionAndWordAndIdNot(request.getDefinition(), def.getWord(), defId);

        if (count > 0) {
            throw new ApiException(DUPLICATED_DEFINITION);
        }

        def.setDefinition(request.getDefinition());
        def.setPart(request.getPart());
        defRepository.save(def);

        return DefDto.fromEntity(def);
    }

    @Transactional
    @PreAuthorize("@defAccessHandler.ownershipCheck(#defId)")
    public DefDto deleteDef(UUID defId) {
        Def def = defRepository.findById(defId)
                .orElseThrow(() -> new ApiException(NO_DEF));

        defRepository.delete(def);

        return DefDto.fromEntity(def);
    }
}

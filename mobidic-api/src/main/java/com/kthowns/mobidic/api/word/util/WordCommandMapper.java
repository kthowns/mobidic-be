package com.kthowns.mobidic.api.word.util;

import com.kthowns.mobidic.api.word.dto.request.AddWordRequestDto;
import com.kthowns.mobidic.api.word.dto.request.UpdateWordAndDefinitionsRequestDto;
import com.kthowns.mobidic.domain.word.command.AddWordCommand;
import com.kthowns.mobidic.domain.word.command.UpdateWordCommand;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WordCommandMapper {
    public AddWordCommand toAddWordCommand(AddWordRequestDto request) {
        return AddWordCommand
                .of(request.getExpression());
    }

    public UpdateWordCommand toUpdateWordCommand(UpdateWordAndDefinitionsRequestDto request, UUID wordId) {
        return UpdateWordCommand
                .of(wordId, request.getExpression());
    }
}

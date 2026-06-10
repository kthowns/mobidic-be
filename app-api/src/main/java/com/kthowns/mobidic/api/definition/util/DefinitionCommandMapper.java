package com.kthowns.mobidic.api.definition.util;

import com.kthowns.mobidic.api.definition.dto.request.AddDefinitionRequestDto;
import com.kthowns.mobidic.api.definition.dto.request.UpdateDefinitionRequestDto;
import com.kthowns.mobidic.domain.definition.command.AddDefinitionCommand;
import com.kthowns.mobidic.domain.definition.command.UpdateDefinitionCommand;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class DefinitionCommandMapper {
    public List<AddDefinitionCommand> toAddDefinitionCommands(List<AddDefinitionRequestDto> requests) {
        return requests != null ?
                requests.stream()
                        .map((d) -> AddDefinitionCommand.of(d.getMeaning(), d.getPart()))
                        .toList()
                : List.of();
    }

    public List<UpdateDefinitionCommand> toUpdateDefinitionCommands(List<UpdateDefinitionRequestDto> requests, UUID wordId) {
        return requests != null ?
                requests.stream()
                        .map((d) -> UpdateDefinitionCommand
                                .of(d.getId(), wordId, d.getMeaning(), d.getPart()))
                        .toList()
                : List.of();
    }
}

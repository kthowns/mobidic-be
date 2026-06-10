package com.kthowns.mobidic.domain.definition.util;

import com.kthowns.mobidic.domain.definition.command.UpdateDefinitionCommand;
import com.kthowns.mobidic.domain.definition.model.Definition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class DefinitionMapper {
    public List<Definition> mapToUpdated(List<Definition> existing, List<UpdateDefinitionCommand> commands) {
        Map<UUID, UpdateDefinitionCommand> commandMap = commands.stream()
                .filter(c -> c.id() != null)
                .collect(Collectors.toMap(UpdateDefinitionCommand::id, c -> c));

        return existing.stream()
                .map(def -> {
                    UpdateDefinitionCommand command = commandMap.get(def.id());
                    return def.update(command.meaning(), command.part());
                })
                .toList();
    }
}

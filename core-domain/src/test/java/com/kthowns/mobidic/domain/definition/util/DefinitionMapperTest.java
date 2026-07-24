package com.kthowns.mobidic.domain.definition.util;

import com.kthowns.mobidic.domain.definition.command.UpdateDefinitionCommand;
import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.domain.global.model.AuditTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DefinitionMapperTest {

    private final DefinitionMapper definitionMapper = new DefinitionMapper();

    @Test
    @DisplayName("mapToUpdated 테스트 - ID 매칭을 통한 모델 업데이트 검증")
    void mapToUpdatedTest() {
        // Given
        UUID wordId = UUID.randomUUID();
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        List<Definition> existing = List.of(
                new Definition(id1, wordId, "기존의미1", PartOfSpeech.NOUN, AuditTime.create()),
                new Definition(id2, wordId, "기존의미2", PartOfSpeech.NOUN, AuditTime.create())
        );

        List<UpdateDefinitionCommand> commands = List.of(
                new UpdateDefinitionCommand(id1, wordId, "수정의미1", PartOfSpeech.VERB),
                new UpdateDefinitionCommand(id2, wordId, "수정의미2", PartOfSpeech.ADJECTIVE)
        );

        // When
        List<Definition> updated = definitionMapper.mapToUpdated(existing, commands);

        // Then
        assertThat(updated).hasSize(2);
        
        Definition res1 = updated.stream().filter(d -> d.id().equals(id1)).findFirst().orElseThrow();
        assertThat(res1.meaning()).isEqualTo("수정의미1");
        assertThat(res1.part()).isEqualTo(PartOfSpeech.VERB);

        Definition res2 = updated.stream().filter(d -> d.id().equals(id2)).findFirst().orElseThrow();
        assertThat(res2.meaning()).isEqualTo("수정의미2");
        assertThat(res2.part()).isEqualTo(PartOfSpeech.ADJECTIVE);
    }
}

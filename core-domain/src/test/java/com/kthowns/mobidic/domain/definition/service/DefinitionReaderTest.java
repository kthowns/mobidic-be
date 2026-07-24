package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import com.kthowns.mobidic.domain.global.model.AuditTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DefinitionReaderTest {

    @Mock
    private DefinitionRepository definitionRepository;

    @InjectMocks
    private DefinitionReader definitionReader;

    private final UUID userId = UUID.randomUUID();
    private final UUID wordId = UUID.randomUUID();

    @Test
    @DisplayName("readByWordId 테스트 - 성공")
    void readByWordIdTest() {
        // Given
        List<Definition> expected = List.of(new Definition(UUID.randomUUID(), wordId, "의미", PartOfSpeech.NOUN, AuditTime.create()));
        given(definitionRepository.readByWordId(wordId, userId)).willReturn(expected);

        // When
        List<Definition> result = definitionReader.readByWordId(wordId, userId);

        // Then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("readByIdAndUserId 테스트 - 성공")
    void readByIdAndUserIdTest() {
        // Given
        UUID defId = UUID.randomUUID();
        Definition expected = new Definition(defId, wordId, "의미", PartOfSpeech.NOUN, AuditTime.create());
        given(definitionRepository.readByIdAndUserId(defId, userId)).willReturn(Optional.of(expected));

        // When
        Definition result = definitionReader.readByIdAndUserId(defId, userId);

        // Then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("readByIdsAndWordIdAndUserId 테스트 - 모든 ID 존재 시 성공")
    void readByIdsSuccess() {
        // Given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<UUID> ids = List.of(id1, id2);
        List<Definition> expected = List.of(
                new Definition(id1, wordId, "의미1", PartOfSpeech.NOUN, AuditTime.create()),
                new Definition(id2, wordId, "의미2", PartOfSpeech.VERB, AuditTime.create())
        );
        given(definitionRepository.readByIdsAndWordIdAndUserId(ids, wordId, userId)).willReturn(expected);

        // When
        List<Definition> result = definitionReader.readByIdsAndWordIdAndUserId(ids, wordId, userId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Definition::id).containsExactlyInAnyOrder(id1, id2);
    }

    @Test
    @DisplayName("readByIdsAndWordIdAndUserId 테스트 - 일부 ID 누락 시 예외 발생")
    void readByIdsFail_MissingId() {
        // Given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<UUID> ids = List.of(id1, id2);
        List<Definition> found = List.of(new Definition(id1, wordId, "의미1", PartOfSpeech.NOUN, AuditTime.create())); // id2 누락
        given(definitionRepository.readByIdsAndWordIdAndUserId(ids, wordId, userId)).willReturn(found);

        // When & Then
        assertThatThrownBy(() -> definitionReader.readByIdsAndWordIdAndUserId(ids, wordId, userId))
                .isInstanceOf(ApiException.class);
    }
}

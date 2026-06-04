package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefinitionRemoverTest {

    @Mock
    private DefinitionRepository definitionRepository;

    @InjectMocks
    private DefinitionRemover definitionRemover;

    @Test
    @DisplayName("removeAll 테스트 - 정의 목록 삭제 성공")
    void removeAllTest() {
        // Given
        List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());
        UUID wordId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // When
        definitionRemover.removeAll(ids, wordId, userId);

        // Then
        verify(definitionRepository).deleteAll(ids, wordId, userId);
    }
}

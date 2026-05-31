package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefinitionRemoverTest {

    @Mock
    private DefinitionRepository definitionRepository;

    @InjectMocks
    private DefinitionRemover definitionRemover;

    @Test
    @DisplayName("remove 테스트 - 정의 삭제 성공")
    void removeTest() {
        // Given
        UUID definitionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // When
        definitionRemover.remove(definitionId, userId);

        // Then
        verify(definitionRepository).delete(definitionId, userId);
    }
}

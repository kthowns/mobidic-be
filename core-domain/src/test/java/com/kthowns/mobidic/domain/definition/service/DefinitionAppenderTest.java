package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefinitionAppenderTest {

    @Mock
    private DefinitionRepository definitionRepository;

    @InjectMocks
    private DefinitionAppender definitionAppender;

    @Test
    @DisplayName("appendAll 테스트 - 정의 목록 저장 성공")
    void appendAllTest() {
        // Given
        List<Definition> definitions = List.of(
                Definition.create(null, "의미1", null),
                Definition.create(null, "의미2", null)
        );

        // When
        definitionAppender.appendAll(definitions);

        // Then
        verify(definitionRepository).appendAll(definitions);
    }
}

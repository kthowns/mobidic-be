package com.kthowns.mobidic.domain.definition.service;

import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.domain.definition.repository.DefinitionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefinitionAppenderTest {

    @Mock
    private DefinitionRepository definitionRepository;

    @InjectMocks
    private DefinitionAppender definitionAppender;

    @Test
    @DisplayName("append 테스트 - 정의 저장 성공")
    void appendTest() {
        // Given
        UUID wordId = UUID.randomUUID();
        String meaning = "테스트 의미";
        PartOfSpeech part = PartOfSpeech.NOUN;

        // When
        definitionAppender.append(wordId, meaning, part);

        // Then
        verify(definitionRepository).append(any(Definition.class));
    }
}

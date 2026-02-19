package com.kimtaeyang.mobidic.service;

import com.kimtaeyang.mobidic.dictionary.dto.AddDefinitionRequestDto;
import com.kimtaeyang.mobidic.dictionary.dto.DefinitionDto;
import com.kimtaeyang.mobidic.dictionary.entity.Definition;
import com.kimtaeyang.mobidic.dictionary.entity.Word;
import com.kimtaeyang.mobidic.dictionary.repository.DefinitionRepository;
import com.kimtaeyang.mobidic.dictionary.repository.WordRepository;
import com.kimtaeyang.mobidic.dictionary.type.PartOfSpeech;
import com.kimtaeyang.mobidic.dictionary.service.DefinitionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefinitionService.class, DefinitionServiceTest.TestConfig.class})
@ActiveProfiles("dev")
class DefinitionServiceTest {
    @Autowired
    private DefinitionRepository definitionRepository;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private DefinitionService definitionService;

    @Test
    @DisplayName("[DefService] Add def success")
    void addDefinitionSuccess() {
        resetMock();

        UUID defId = UUID.randomUUID();

        AddDefinitionRequestDto request = AddDefinitionRequestDto.builder()
                .definition("definition")
                .part(PartOfSpeech.NOUN)
                .build();

        ArgumentCaptor<Definition> captor =
                ArgumentCaptor.forClass(Definition.class);

        //given
        given(wordRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(Word.class)));
        given(definitionRepository.save(any(Definition.class)))
                .willAnswer(invocation -> {
                    Definition definitionArg = invocation.getArgument(0);
                    definitionArg.setId(defId);
                    return definitionArg;
                });

        //when
        DefinitionDto response = definitionService.addDefinition(UUID.randomUUID(), request);

        //then
        verify(definitionRepository, times(1))
                .save(captor.capture());

        assertEquals(request.getDefinition(), response.getDefinition());
        assertEquals(request.getPart(), response.getPart());
        assertEquals(defId, response.getId());
    }

    @Test
    @DisplayName("[DefService] Get defs by word id success")
    void getDefinitionsByWordIdSuccess() {
        resetMock();

        Definition defaultDefinition = Definition.builder()
                .word(Mockito.mock(Word.class))
                .definition("definition")
                .part(PartOfSpeech.NOUN)
                .build();

        ArrayList<Definition> definitions = new ArrayList<>();
        definitions.add(defaultDefinition);

        //given
        given(wordRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(Word.class)));
        given(definitionRepository.findByWord(any(Word.class)))
                .willReturn(definitions);

        //when
        List<DefinitionDto> response = definitionService.getDefinitionsByWordId(UUID.randomUUID());

        //then
        assertEquals(definitions.getFirst().getWord().getId(), response.getFirst().getWordId());
        assertEquals(definitions.getFirst().getDefinition(), response.getFirst().getDefinition());
        assertEquals(definitions.getFirst().getPart(), response.getFirst().getPart());
    }

    @Test
    @DisplayName("[DefService] Update def success")
    void updateWordSuccess() {
        resetMock();

        UUID defId = UUID.randomUUID();

        Definition defaultDefinition = Definition.builder()
                .id(defId)
                .word(Mockito.mock(Word.class))
                .definition("definition")
                .part(PartOfSpeech.NOUN)
                .build();

        AddDefinitionRequestDto request =
                AddDefinitionRequestDto.builder()
                        .definition("definition2")
                        .part(PartOfSpeech.VERB)
                        .build();

        ArgumentCaptor<Definition> captor =
                ArgumentCaptor.forClass(Definition.class);

        //given
        given(definitionRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(defaultDefinition));
        given(definitionRepository.save(any(Definition.class)))
                .willAnswer(invocation -> {
                    Definition definitionArg = invocation.getArgument(0);
                    definitionArg.setDefinition(request.getDefinition());
                    definitionArg.setPart(request.getPart());
                    return definitionArg;
                });

        //when
        DefinitionDto response =
                definitionService.updateDefinition(defId, request);

        //then
        verify(definitionRepository, times(1))
                .save(captor.capture());
        assertEquals(defId, response.getId());
        assertEquals(request.getDefinition(), response.getDefinition());
        assertEquals(request.getPart(), response.getPart());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public DefinitionRepository defRepository() {
            return Mockito.mock(DefinitionRepository.class);
        }

        @Bean
        public WordRepository wordRepository() {
            return Mockito.mock(WordRepository.class);
        }
    }

    private void resetMock() {
        Mockito.reset(definitionRepository, wordRepository);
    }
}
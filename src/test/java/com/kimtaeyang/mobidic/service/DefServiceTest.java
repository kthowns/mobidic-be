package com.kimtaeyang.mobidic.service;

import com.kimtaeyang.mobidic.dto.AddDefRequestDto;
import com.kimtaeyang.mobidic.dto.DefDto;
import com.kimtaeyang.mobidic.entity.Def;
import com.kimtaeyang.mobidic.entity.Word;
import com.kimtaeyang.mobidic.repository.DefRepository;
import com.kimtaeyang.mobidic.repository.WordRepository;
import com.kimtaeyang.mobidic.type.PartOfSpeech;
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
@ContextConfiguration(classes = {DefService.class, DefServiceTest.TestConfig.class})
@ActiveProfiles("dev")
class DefServiceTest {
    @Autowired
    private DefRepository defRepository;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private DefService defService;

    @Test
    @DisplayName("[DefService] Add def success")
    void addDefSuccess() {
        resetMock();

        UUID defId = UUID.randomUUID();

        AddDefRequestDto request = AddDefRequestDto.builder()
                .definition("definition")
                .part(PartOfSpeech.NOUN)
                .build();

        ArgumentCaptor<Def> captor =
                ArgumentCaptor.forClass(Def.class);

        //given
        given(wordRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(Word.class)));
        given(defRepository.save(any(Def.class)))
                .willAnswer(invocation -> {
                    Def defArg = invocation.getArgument(0);
                    defArg.setId(defId);
                    return defArg;
                });

        //when
        DefDto response = defService.addDef(UUID.randomUUID(), request);

        //then
        verify(defRepository, times(1))
                .save(captor.capture());

        assertEquals(request.getDefinition(), response.getDefinition());
        assertEquals(request.getPart(), response.getPart());
        assertEquals(defId, response.getId());
    }

    @Test
    @DisplayName("[DefService] Get defs by word id success")
    void getDefsByWordIdSuccess() {
        resetMock();

        Def defaultDef = Def.builder()
                .word(Mockito.mock(Word.class))
                .definition("definition")
                .part(PartOfSpeech.NOUN)
                .build();

        ArrayList<Def> defs = new ArrayList<>();
        defs.add(defaultDef);

        //given
        given(wordRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(Word.class)));
        given(defRepository.findByWord(any(Word.class)))
                .willReturn(defs);

        //when
        List<DefDto> response = defService.getDefsByWordId(UUID.randomUUID());

        //then
        assertEquals(defs.getFirst().getWord().getId(), response.getFirst().getWordId());
        assertEquals(defs.getFirst().getDefinition(), response.getFirst().getDefinition());
        assertEquals(defs.getFirst().getPart(), response.getFirst().getPart());
    }

    @Test
    @DisplayName("[DefService] Update def success")
    void updateWordSuccess() {
        resetMock();

        UUID defId = UUID.randomUUID();

        Def defaultDef = Def.builder()
                .id(defId)
                .word(Mockito.mock(Word.class))
                .definition("definition")
                .part(PartOfSpeech.NOUN)
                .build();

        AddDefRequestDto request =
                AddDefRequestDto.builder()
                        .definition("definition2")
                        .part(PartOfSpeech.VERB)
                        .build();

        ArgumentCaptor<Def> captor =
                ArgumentCaptor.forClass(Def.class);

        //given
        given(defRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(defaultDef));
        given(defRepository.save(any(Def.class)))
                .willAnswer(invocation -> {
                    Def defArg = invocation.getArgument(0);
                    defArg.setDefinition(request.getDefinition());
                    defArg.setPart(request.getPart());
                    return defArg;
                });

        //when
        DefDto response =
                defService.updateDef(defId, request);

        //then
        verify(defRepository, times(1))
                .save(captor.capture());
        assertEquals(defId, response.getId());
        assertEquals(request.getDefinition(), response.getDefinition());
        assertEquals(request.getPart(), response.getPart());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public DefRepository defRepository() {
            return Mockito.mock(DefRepository.class);
        }

        @Bean
        public WordRepository wordRepository() {
            return Mockito.mock(WordRepository.class);
        }
    }

    private void resetMock() {
        Mockito.reset(defRepository, wordRepository);
    }
}
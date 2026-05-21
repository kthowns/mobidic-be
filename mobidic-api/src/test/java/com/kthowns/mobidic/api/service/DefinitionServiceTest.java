package com.kthowns.mobidic.api.service;

import com.kthowns.mobidic.api.config.ServiceTestConfig;
import com.kthowns.mobidic.api.dictionary.dto.request.AddDefinitionRequestDto;
import com.kthowns.mobidic.domain.dictionary.model.Definition;
import com.kthowns.mobidic.storage.dictionary.jpaentity.DefinitionJpaEntity;
import com.kthowns.mobidic.storage.dictionary.jpaentity.WordJpaEntity;
import com.kthowns.mobidic.storage.dictionary.jparepository.DefinitionJpaRepository;
import com.kthowns.mobidic.storage.dictionary.jparepository.WordJpaRepository;
import com.kthowns.mobidic.domain.dictionary.service.DefinitionService;
import com.kthowns.mobidic.domain.dictionary.model.PartOfSpeech;
import com.kthowns.mobidic.storage.user.jpaentity.UserJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefinitionService.class, ServiceTestConfig.class})
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "jwt.secret=f825308ac5df56907db5835775baf3e4594526f127cb8d9bca70b435d596d424",
        "jwt.exp=3600000"
})
class DefinitionServiceTest {
    @Autowired
    private DefinitionJpaRepository definitionRepository;

    @Autowired
    private WordJpaRepository wordRepository;

    @Autowired
    private DefinitionService definitionService;

    private final UserJpaEntity testUserJpaEntity = UserJpaEntity.builder()
            .id(UUID.randomUUID())
            .build();

    @Test
    @DisplayName("[DefService] Add def success")
    void addDefinitionSuccess() {
        resetMock();

        UUID defId = UUID.randomUUID();

        AddDefinitionRequestDto request = AddDefinitionRequestDto.builder()
                .meaning("definition")
                .part(PartOfSpeech.NOUN)
                .build();

        ArgumentCaptor<DefinitionJpaEntity> captor =
                ArgumentCaptor.forClass(DefinitionJpaEntity.class);

        //given
        given(wordRepository.findByIdAndVocabulary_User_Id(any(UUID.class), any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(WordJpaEntity.class)));
        given(definitionRepository.save(any(DefinitionJpaEntity.class)))
                .willAnswer(invocation -> {
                    DefinitionJpaEntity definitionArg = invocation.getArgument(0);
                    definitionArg.setId(defId);
                    return definitionArg;
                });

        //when
        Definition response = definitionService.addDefinition(testUserJpaEntity, UUID.randomUUID(), request);

        //then
        verify(definitionRepository, times(1))
                .save(captor.capture());

        assertEquals(request.getMeaning(), response.getMeaning());
        assertEquals(request.getPart(), response.getPart());
        assertEquals(defId, response.getId());
    }

    @Test
    @DisplayName("[DefService] Get defs by word id success")
    void getDefinitionsByWordIdSuccess() {
        resetMock();

        DefinitionJpaEntity defaultDefinition = DefinitionJpaEntity.builder()
                .word(Mockito.mock(WordJpaEntity.class))
                .meaning("definition")
                .part(PartOfSpeech.NOUN)
                .build();

        ArrayList<DefinitionJpaEntity> definitions = new ArrayList<>();
        definitions.add(defaultDefinition);

        //given
        given(wordRepository.findByIdAndVocabulary_User_Id(any(UUID.class), any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(WordJpaEntity.class)));
        given(definitionRepository.findByWord(any(WordJpaEntity.class)))
                .willReturn(definitions);

        //when
        List<Definition> response = definitionService.getDefinitionsByWordId(testUserJpaEntity, UUID.randomUUID());

        //then
        assertEquals(definitions.getFirst().getMeaning(), response.getFirst().getMeaning());
        assertEquals(definitions.getFirst().getPart(), response.getFirst().getPart());
    }

    @Test
    @DisplayName("[DefService] Update def success")
    void updateWordSuccess() {
        resetMock();

        UUID defId = UUID.randomUUID();

        DefinitionJpaEntity defaultDefinition = DefinitionJpaEntity.builder()
                .id(defId)
                .word(Mockito.mock(WordJpaEntity.class))
                .meaning("definition")
                .part(PartOfSpeech.NOUN)
                .build();

        AddDefinitionRequestDto request =
                AddDefinitionRequestDto.builder()
                        .meaning("definition2")
                        .part(PartOfSpeech.VERB)
                        .build();

        ArgumentCaptor<DefinitionJpaEntity> captor =
                ArgumentCaptor.forClass(DefinitionJpaEntity.class);

        //given
        given(definitionRepository.findByIdAndWord_Vocabulary_User_Id(any(UUID.class), any(UUID.class)))
                .willReturn(Optional.of(defaultDefinition));
        given(definitionRepository.save(any(DefinitionJpaEntity.class)))
                .willAnswer(invocation -> {
                    DefinitionJpaEntity definitionArg = invocation.getArgument(0);
                    definitionArg.setMeaning(request.getMeaning());
                    definitionArg.setPart(request.getPart());
                    return definitionArg;
                });

        //when
        Definition response =
                definitionService.updateDefinition(testUserJpaEntity, defId, request);

        //then
        verify(definitionRepository, times(1))
                .save(captor.capture());
        assertEquals(defId, response.getId());
        assertEquals(request.getMeaning(), response.getMeaning());
        assertEquals(request.getPart(), response.getPart());
    }

    private void resetMock() {
        Mockito.reset(definitionRepository, wordRepository);
    }
}
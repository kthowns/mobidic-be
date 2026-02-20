package com.kimtaeyang.mobidic.service;

import com.kimtaeyang.mobidic.config.ServiceTestConfig;
import com.kimtaeyang.mobidic.dictionary.dto.AddVocabularyRequestDto;
import com.kimtaeyang.mobidic.dictionary.dto.VocabularyDto;
import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
import com.kimtaeyang.mobidic.dictionary.repository.VocabularyRepository;
import com.kimtaeyang.mobidic.dictionary.service.VocabularyService;
import com.kimtaeyang.mobidic.user.entity.User;
import com.kimtaeyang.mobidic.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {VocabularyService.class, ServiceTestConfig.class})
@ActiveProfiles("dev")
class VocabularyServiceTest {
    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VocabularyService vocabularyService;

    @Test
    @DisplayName("[VocabService] Add vocab success")
    void addVocabularySuccess() {
        resetMock();

        UUID vocabId = UUID.randomUUID();

        AddVocabularyRequestDto request = AddVocabularyRequestDto.builder()
                .title("title")
                .description("description")
                .build();
        ArgumentCaptor<Vocabulary> captor =
                ArgumentCaptor.forClass(Vocabulary.class);

        //given
        given(userRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(User.class)));
        given(vocabularyRepository.countByTitleAndUser(anyString(), any(User.class)))
                .willReturn(0);
        given(vocabularyRepository.save(any(Vocabulary.class)))
                .willAnswer(invocation -> {
                    Vocabulary vocabularyArg = invocation.getArgument(0);
                    vocabularyArg.setId(vocabId);
                    return vocabularyArg;
                });

        //when
        VocabularyDto response = vocabularyService.addVocabulary(UUID.randomUUID(), request);

        //then
        verify(vocabularyRepository, times(1))
                .save(captor.capture());

        assertEquals(request.getTitle(), response.getTitle());
        assertEquals(request.getDescription(), response.getDescription());
        assertEquals(vocabId, response.getId());
    }

    @Test
    @DisplayName("[VocabService] Get vocabs by member id success")
    void getVocabulariesByUserIdSuccess() {
        resetMock();

        Vocabulary defaultVocabulary = Vocabulary.builder()
                .user(Mockito.mock(User.class))
                .title("title")
                .description("description")
                .build();

        ArrayList<Vocabulary> vocabularies = new ArrayList<>();
        vocabularies.add(defaultVocabulary);

        //given
        given(userRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(User.class)));
        given(vocabularyRepository.findByUser(any(User.class)))
                .willReturn(vocabularies);

        //when
        List<VocabularyDto> response = vocabularyService.getVocabulariesByUserId(UUID.randomUUID());

        //then
        assertEquals(vocabularies.getFirst().getUser().getId(), response.getFirst().getUserId());
        assertEquals(vocabularies.getFirst().getTitle(), response.getFirst().getTitle());
        assertEquals(vocabularies.getFirst().getDescription(), response.getFirst().getDescription());
    }

    @Test
    @DisplayName("[VocabService] Get vocab by vocab id success")
    void getVocabByVocabIdSuccess() {
        resetMock();

        UUID vocabId = UUID.randomUUID();

        Vocabulary defaultVocabulary = Vocabulary.builder()
                .id(vocabId)
                .user(Mockito.mock(User.class))
                .title("title")
                .description("description")
                .build();

        //given
        given(vocabularyRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(defaultVocabulary));

        //when
        VocabularyDto response = vocabularyService.getVocabularyById(vocabId);

        //then
        assertEquals(vocabId, response.getId());
        assertEquals(defaultVocabulary.getTitle(), response.getTitle());
        assertEquals(defaultVocabulary.getDescription(), response.getDescription());
    }

    @Test
    @DisplayName("[VocabService] Update vocab success")
    void updateVocabularySuccess() {
        resetMock();

        UUID vocabId = UUID.randomUUID();

        Vocabulary defaultVocabulary = Vocabulary.builder()
                .id(vocabId)
                .user(Mockito.mock(User.class))
                .title("title")
                .description("description")
                .build();

        AddVocabularyRequestDto request =
                AddVocabularyRequestDto.builder()
                        .title("title2")
                        .description("description2")
                        .build();

        ArgumentCaptor<Vocabulary> captor =
                ArgumentCaptor.forClass(Vocabulary.class);

        //given
        given(vocabularyRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(defaultVocabulary));
        given(vocabularyRepository.countByTitleAndUserAndIdNot(anyString(), any(User.class), any(UUID.class)))
                .willReturn(0);
        given(vocabularyRepository.save(any(Vocabulary.class)))
                .willAnswer(invocation -> {
                    Vocabulary vocabularyArg = invocation.getArgument(0);
                    vocabularyArg.setTitle(request.getTitle());
                    vocabularyArg.setDescription(request.getDescription());
                    return vocabularyArg;
                });

        //when
        VocabularyDto response =
                vocabularyService.updateVocabulary(vocabId, request);

        //then
        verify(vocabularyRepository, times(1))
                .save(captor.capture());
        assertEquals(vocabId, response.getId());
        assertEquals(request.getTitle(), response.getTitle());
        assertEquals(request.getDescription(), response.getDescription());
    }

    private void resetMock() {
        Mockito.reset(vocabularyRepository, userRepository);
    }
}
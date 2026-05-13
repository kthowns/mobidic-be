package com.kthowns.mobidic.service;

import com.kthowns.mobidic.config.ServiceTestConfig;
import com.kthowns.mobidic.dictionary.dto.AddVocabularyRequestDto;
import com.kthowns.mobidic.dictionary.dto.VocabularyDetail;
import com.kthowns.mobidic.dictionary.dto.VocabularyDto;
import com.kthowns.mobidic.dictionary.entity.Vocabulary;
import com.kthowns.mobidic.dictionary.repository.VocabularyRepository;
import com.kthowns.mobidic.dictionary.service.VocabularyService;
import com.kthowns.mobidic.user.entity.User;
import com.kthowns.mobidic.user.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {VocabularyService.class, ServiceTestConfig.class})
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "jwt.secret=f825308ac5df56907db5835775baf3e4594526f127cb8d9bca70b435d596d424",
        "jwt.exp=3600000"
})
class VocabularyServiceTest {
    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VocabularyService vocabularyService;

    private final User testUser = User.builder()
            .id(UUID.randomUUID())
            .build();

    @Test
    @DisplayName("[VocabService] Add vocab success")
    void addVocabularySuccess() {
        resetMock();

        UUID vocabId = UUID.randomUUID();

        AddVocabularyRequestDto request = AddVocabularyRequestDto.builder()
                .title("title")
                .description("description")
                .build();
        Vocabulary savedVocabulary = Vocabulary.builder()
                .id(vocabId)
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
        ArgumentCaptor<Vocabulary> captor =
                ArgumentCaptor.forClass(Vocabulary.class);

        //given
        given(vocabularyRepository.existsByTitleAndUser(anyString(), any(User.class)))
                .willReturn(false);
        given(vocabularyRepository.save(any(Vocabulary.class)))
                .willReturn(savedVocabulary);

        //when
        VocabularyDto response = vocabularyService.addVocabulary(testUser, request);

        //then
        verify(vocabularyRepository, times(1))
                .save(captor.capture());

        assertEquals(request.getTitle(), response.getTitle());
        assertEquals(request.getDescription(), response.getDescription());
        assertEquals(vocabId, response.getId());
    }

    @Test
    @DisplayName("[VocabService] Get vocabs success")
    void getVocabularyDetailsSuccess() {
        resetMock();

        Vocabulary defaultVocabulary = Vocabulary.builder()
                .user(testUser)
                .title("title")
                .description("description")
                .build();

        ArrayList<Vocabulary> vocabularies = new ArrayList<>();
        vocabularies.add(defaultVocabulary);

        //given
        given(vocabularyRepository.findVocabularyDetails(any(UUID.class)))
                .willReturn(List.of(
                        new VocabularyDetail(VocabularyDto.fromEntity(defaultVocabulary), 0.0, 0.0)));

        //when
        List<VocabularyDetail> response = vocabularyService.getVocabularyDetails(testUser);

        //then
        assertEquals(vocabularies.getFirst().getTitle(), response.getFirst().vocabulary().getTitle());
        assertEquals(vocabularies.getFirst().getDescription(), response.getFirst().vocabulary().getDescription());
    }

    @Test
    @DisplayName("[VocabService] Get vocab by vocab id success")
    void getVocabByVocabIdSuccess() {
        resetMock();

        UUID vocabId = UUID.randomUUID();

        VocabularyDetail defaultVocabulary =
                VocabularyDetail.builder()
                        .vocabulary(VocabularyDto.builder()
                                .id(vocabId)
                                .title("title")
                                .description("description")
                                .build())
                        .build();

        //given
        given(vocabularyRepository.findVocabularyDetail(any(UUID.class), any(UUID.class)))
                .willReturn(Optional.of(defaultVocabulary));

        //when
        VocabularyDetail response = vocabularyService.getVocabularyById(testUser, vocabId);

        //then
        assertEquals(vocabId, response.vocabulary().getId());
        assertEquals(defaultVocabulary.vocabulary().getTitle(), response.vocabulary().getTitle());
        assertEquals(defaultVocabulary.vocabulary().getDescription(), response.vocabulary().getDescription());
    }

    @Test
    @DisplayName("[VocabService] Update vocab success")
    void updateVocabularySuccess() {
        resetMock();

        UUID vocabId = UUID.randomUUID();

        Vocabulary defaultVocabulary = Vocabulary.builder()
                .id(vocabId)
                .user(testUser)
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
        given(vocabularyRepository.findForUpdate(any(UUID.class), any(UUID.class)))
                .willReturn(Optional.of(defaultVocabulary));
        given(vocabularyRepository.existsByTitleAndUserAndIdNot(anyString(), any(User.class), any(UUID.class)))
                .willReturn(false);
        given(vocabularyRepository.save(any(Vocabulary.class)))
                .willAnswer(invocation -> {
                    Vocabulary vocabularyArg = invocation.getArgument(0);
                    vocabularyArg.setTitle(request.getTitle());
                    vocabularyArg.setDescription(request.getDescription());
                    return vocabularyArg;
                });

        //when
        VocabularyDto response =
                vocabularyService.updateVocabulary(testUser, vocabId, request);

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
package com.kthowns.mobidic.api.service;

import com.kthowns.mobidic.api.config.ServiceTestConfig;
import com.kthowns.mobidic.api.vocabulary.dto.request.AddVocabularyRequestDto;
import com.kthowns.mobidic.domain.vocabulary.model.VocabularyDetail;
import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.storage.vocabulary.jpaentity.VocabularyJpaEntity;
import com.kthowns.mobidic.storage.vocabulary.jparepository.VocabularyJpaRepository;
import com.kthowns.mobidic.domain.vocabulary.service.VocabularyService;
import com.kthowns.mobidic.storage.user.jpaentity.UserJpaEntity;
import com.kthowns.mobidic.storage.user.jparepository.UserJpaRepository;
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
    private VocabularyJpaRepository vocabularyRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private VocabularyService vocabularyService;

    private final UserJpaEntity testUserJpaEntity = UserJpaEntity.builder()
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
        VocabularyJpaEntity savedVocabulary = VocabularyJpaEntity.builder()
                .id(vocabId)
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
        ArgumentCaptor<VocabularyJpaEntity> captor =
                ArgumentCaptor.forClass(VocabularyJpaEntity.class);

        //given
        given(vocabularyRepository.existsByTitleAndUser(anyString(), any(UserJpaEntity.class)))
                .willReturn(false);
        given(vocabularyRepository.save(any(VocabularyJpaEntity.class)))
                .willReturn(savedVocabulary);

        //when
        Vocabulary response = vocabularyService.addVocabulary(testUserJpaEntity, request);

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

        VocabularyJpaEntity defaultVocabulary = VocabularyJpaEntity.builder()
                .user(testUserJpaEntity)
                .title("title")
                .description("description")
                .build();

        ArrayList<VocabularyJpaEntity> vocabularies = new ArrayList<>();
        vocabularies.add(defaultVocabulary);

        //given
        given(vocabularyRepository.findVocabularyDetails(any(UUID.class)))
                .willReturn(List.of(
                        new VocabularyDetail(Vocabulary.fromEntity(defaultVocabulary), 0.0, 0.0)));

        //when
        List<VocabularyDetail> response = vocabularyService.getVocabularyDetails(testUserJpaEntity);

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
                        .vocabulary(Vocabulary.builder()
                                .id(vocabId)
                                .title("title")
                                .description("description")
                                .build())
                        .build();

        //given
        given(vocabularyRepository.findVocabularyDetail(any(UUID.class), any(UUID.class)))
                .willReturn(Optional.of(defaultVocabulary));

        //when
        VocabularyDetail response = vocabularyService.getVocabularyById(testUserJpaEntity, vocabId);

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

        VocabularyJpaEntity defaultVocabulary = VocabularyJpaEntity.builder()
                .id(vocabId)
                .user(testUserJpaEntity)
                .title("title")
                .description("description")
                .build();

        AddVocabularyRequestDto request =
                AddVocabularyRequestDto.builder()
                        .title("title2")
                        .description("description2")
                        .build();

        ArgumentCaptor<VocabularyJpaEntity> captor =
                ArgumentCaptor.forClass(VocabularyJpaEntity.class);

        //given
        given(vocabularyRepository.findForUpdate(any(UUID.class), any(UUID.class)))
                .willReturn(Optional.of(defaultVocabulary));
        given(vocabularyRepository.existsByTitleAndUserAndIdNot(anyString(), any(UserJpaEntity.class), any(UUID.class)))
                .willReturn(false);
        given(vocabularyRepository.save(any(VocabularyJpaEntity.class)))
                .willAnswer(invocation -> {
                    VocabularyJpaEntity vocabularyArg = invocation.getArgument(0);
                    vocabularyArg.setTitle(request.getTitle());
                    vocabularyArg.setDescription(request.getDescription());
                    return vocabularyArg;
                });

        //when
        Vocabulary response =
                vocabularyService.updateVocabulary(testUserJpaEntity, vocabId, request);

        //then
        verify(vocabularyRepository, times(1))
                .save(captor.capture());
        assertEquals(vocabId, response.getId());
        assertEquals(request.getTitle(), response.getTitle());
        assertEquals(request.getDescription(), response.getDescription());
    }

    private void resetMock() {
        Mockito.reset(vocabularyRepository, userJpaRepository);
    }
}
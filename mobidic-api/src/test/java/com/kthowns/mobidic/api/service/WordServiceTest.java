package com.kthowns.mobidic.api.service;

import com.kthowns.mobidic.api.config.ServiceTestConfig;
import com.kthowns.mobidic.api.dictionary.dto.request.AddWordRequestDto;
import com.kthowns.mobidic.domain.dictionary.model.WordDetail;
import com.kthowns.mobidic.domain.dictionary.model.Word;
import com.kthowns.mobidic.storage.dictionary.jpaentity.VocabularyJpaEntity;
import com.kthowns.mobidic.storage.dictionary.jpaentity.WordJpaEntity;
import com.kthowns.mobidic.storage.dictionary.jparepository.DefinitionJpaRepository;
import com.kthowns.mobidic.storage.dictionary.jparepository.VocabularyJpaRepository;
import com.kthowns.mobidic.storage.dictionary.jparepository.WordJpaRepository;
import com.kthowns.mobidic.domain.dictionary.service.WordService;
import com.kthowns.mobidic.storage.statistic.jparepository.WordStatisticJpaRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {WordService.class, ServiceTestConfig.class})
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "jwt.secret=f825308ac5df56907db5835775baf3e4594526f127cb8d9bca70b435d596d424",
        "jwt.exp=3600000"
})
class WordServiceTest {
    @Autowired
    private WordJpaRepository wordRepository;
    @Autowired
    private VocabularyJpaRepository vocabularyRepository;

    @Autowired
    private DefinitionJpaRepository definitionRepository;

    @Autowired
    private WordStatisticJpaRepository wordStatisticRepository;

    @Autowired
    private WordService wordService;

    private final UserJpaEntity testUserJpaEntity = UserJpaEntity.builder()
            .id(UUID.randomUUID())
            .build();

    @Test
    @DisplayName("[WordService] Add vocab success")
    void addWordSuccess() {
        resetMock();

        UUID wordId = UUID.randomUUID();

        AddWordRequestDto request = AddWordRequestDto.builder()
                .expression("test")
                .build();

        ArgumentCaptor<WordJpaEntity> captor =
                ArgumentCaptor.forClass(WordJpaEntity.class);

        //given
        given(vocabularyRepository.findForUpdate(any(UUID.class), any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(VocabularyJpaEntity.class)));
        given(wordRepository.existsByExpressionAndVocabulary(anyString(), any(VocabularyJpaEntity.class)))
                .willReturn(false);
        given(wordRepository.save(any(WordJpaEntity.class)))
                .willAnswer(invocation -> {
                    WordJpaEntity wordArg = invocation.getArgument(0);
                    wordArg.setId(wordId);
                    return wordArg;
                });

        //when
        Word response = wordService.addWord(testUserJpaEntity, UUID.randomUUID(), request);

        //then
        verify(wordRepository, times(1))
                .save(captor.capture());

        assertEquals(request.getExpression(), response.getExpression());
        assertEquals(wordId, response.getId());
    }

    @Test
    @DisplayName("[WordService] Get words by vocab id success")
    void getWordDetailsByVocabularyIdSuccess() {
        resetMock();

        WordDetail defaultWordDetail = WordDetail.builder()
                .id(UUID.randomUUID())
                .expression("expression")
                .build();

        List<WordDetail> words = List.of(defaultWordDetail);

        //given
        given(vocabularyRepository.existsByIdAndUser_Id(any(UUID.class), any(UUID.class)))
                .willReturn(true);
        given(wordRepository.findWordDetailsByVocabularyId(any(UUID.class), any(UUID.class), any(boolean.class)))
                .willReturn(words);

        //when
        List<WordDetail> response = wordService.getWordDetailsByVocabularyId(testUserJpaEntity, UUID.randomUUID());

        //then
        assertEquals(words.getFirst().expression(), response.getFirst().expression());
    }

    @Test
    @DisplayName("[WordService] Update word success")
    void updateWordSuccess() {
        resetMock();

        UUID wordId = UUID.randomUUID();

        WordJpaEntity defaultWord = WordJpaEntity.builder()
                .id(wordId)
                .vocabulary(Mockito.mock(VocabularyJpaEntity.class))
                .expression("expression")
                .build();

        AddWordRequestDto request =
                AddWordRequestDto.builder()
                        .expression("expression2")
                        .build();

        ArgumentCaptor<WordJpaEntity> captor =
                ArgumentCaptor.forClass(WordJpaEntity.class);

        //given
        given(wordRepository.findByIdAndVocabulary_User_Id(any(UUID.class), any(UUID.class)))
                .willReturn(Optional.of(defaultWord));
        given(vocabularyRepository.existsByTitleAndUserAndIdNot(anyString(), any(UserJpaEntity.class), any(UUID.class)))
                .willReturn(false);
        given(wordRepository.save(any(WordJpaEntity.class)))
                .willAnswer(invocation -> {
                    WordJpaEntity wordArg = invocation.getArgument(0);
                    wordArg.setExpression(request.getExpression());
                    return wordArg;
                });

        //when
        Word response = wordService.updateWord(testUserJpaEntity, wordId, request);

        //then
        verify(wordRepository, times(1))
                .save(captor.capture());
        assertEquals(wordId, response.getId());
        assertEquals(request.getExpression(), response.getExpression());
    }

    private void resetMock() {
        Mockito.reset(wordRepository, vocabularyRepository, definitionRepository, wordStatisticRepository);
    }
}
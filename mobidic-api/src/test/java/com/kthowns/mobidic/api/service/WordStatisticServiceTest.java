package com.kthowns.mobidic.api.service;

import com.kthowns.mobidic.api.config.ServiceTestConfig;
import com.kthowns.mobidic.storage.dictionary.jpaentity.VocabularyJpaEntity;
import com.kthowns.mobidic.storage.dictionary.jpaentity.WordJpaEntity;
import com.kthowns.mobidic.storage.dictionary.jparepository.VocabularyJpaRepository;
import com.kthowns.mobidic.storage.dictionary.jparepository.WordJpaRepository;
import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.storage.statistic.jpaentity.WordStatisticJpaEntity;
import com.kthowns.mobidic.storage.statistic.jparepository.WordStatisticJpaRepository;
import com.kthowns.mobidic.domain.statistic.service.StatisticService;
import com.kthowns.mobidic.storage.user.jpaentity.UserJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {StatisticService.class, ServiceTestConfig.class})
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "jwt.secret=f825308ac5df56907db5835775baf3e4594526f127cb8d9bca70b435d596d424",
        "jwt.exp=3600000"
})
class WordStatisticServiceTest {
    @Autowired
    private WordStatisticJpaRepository wordStatisticRepository;

    @Autowired
    private WordJpaRepository wordRepository;

    @Autowired
    private VocabularyJpaRepository vocabularyRepository;

    @Autowired
    private StatisticService statisticService;

    private final UserJpaEntity testUserJpaEntity = UserJpaEntity.builder()
            .id(UUID.randomUUID())
            .build();

    @Test
    @DisplayName("[StatisticService] Get rate by word id success")
    void getRateByWordIdSuccess() {
        resetMock();

        UUID wordId = UUID.randomUUID();

        WordStatisticJpaEntity defaultWordStatistic = WordStatisticJpaEntity.builder()
                .word(mock(WordJpaEntity.class))
                .wordId(wordId)
                .correctCount(3L)
                .incorrectCount(5L)
                .isLearned(true)
                .build();

        //given
        given(wordStatisticRepository.findByWordIdAndWord_Vocabulary_User_Id(any(UUID.class), any(UUID.class)))
                .willReturn(Optional.of(defaultWordStatistic));

        //when
        WordStatistic response = statisticService.getWordStatisticById(testUserJpaEntity, UUID.randomUUID());

        //then
        assertEquals(defaultWordStatistic.getWordId(), response.getWordId());
        assertEquals(defaultWordStatistic.isLearned(), response.isLearned());
        assertEquals(defaultWordStatistic.getCorrectCount(), response.getCorrectCount());
        assertEquals(defaultWordStatistic.getIncorrectCount(), response.getIncorrectCount());
    }

    @Test
    @DisplayName("[StatisticService] Get vocab learning rate success")
    public void getVocabLearningRateSuccess() {
        resetMock();

        UUID vocabularyId = UUID.randomUUID();
        Double learningRate = 0.8;

        //given
        given(wordStatisticRepository.getVocabularyLearningRate(any(VocabularyJpaEntity.class)))
                .willReturn(Optional.of(learningRate));
        given(vocabularyRepository.findByIdAndUser_Id(any(UUID.class), any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(VocabularyJpaEntity.class)));
        given(wordRepository.countByVocabulary(any(VocabularyJpaEntity.class)))
                .willReturn(1L);

        //when
        Double foundLearningRate = statisticService.getVocabLearningRate(testUserJpaEntity, vocabularyId);

        //then
        assertEquals(learningRate, foundLearningRate);
    }

    @Test
    @DisplayName("[StatisticService] toggle rate success")
    void toggleRateSuccess() {
        resetMock();

        UUID wordId = UUID.randomUUID();

        WordStatisticJpaEntity defaultStatistic = WordStatisticJpaEntity.builder()
                .wordId(wordId)
                .word(Mockito.mock(WordJpaEntity.class))
                .correctCount(3L)
                .isLearned(true)
                .incorrectCount(4L)
                .build();

        //given
        given(wordRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(WordJpaEntity.class)));
        given(wordStatisticRepository.findForUpdate(any(UUID.class), any(UUID.class)))
                .willReturn(Optional.of(defaultStatistic));
        given(wordStatisticRepository.save(any(WordStatisticJpaEntity.class)))
                .willReturn(Mockito.mock(WordStatisticJpaEntity.class));

        //when
        statisticService.toggleLearnedByWordId(testUserJpaEntity, wordId);

        // then
        assertFalse(defaultStatistic.isLearned());
        verify(wordStatisticRepository, times(1)).findForUpdate(wordId, testUserJpaEntity.getId());
        verify(wordStatisticRepository, never()).save(any());
    }

    private void resetMock() {
        Mockito.reset(wordStatisticRepository, wordRepository);
    }
}
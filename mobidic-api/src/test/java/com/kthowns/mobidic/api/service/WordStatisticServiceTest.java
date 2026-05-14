package com.kthowns.mobidic.api.service;

import com.kthowns.mobidic.api.config.ServiceTestConfig;
import com.kthowns.mobidic.api.dictionary.entity.Vocabulary;
import com.kthowns.mobidic.api.dictionary.entity.Word;
import com.kthowns.mobidic.api.dictionary.repository.VocabularyRepository;
import com.kthowns.mobidic.api.dictionary.repository.WordRepository;
import com.kthowns.mobidic.api.dto.common.statistic.StatisticDto;
import com.kthowns.mobidic.api.statistic.entity.WordStatistic;
import com.kthowns.mobidic.api.statistic.repository.WordStatisticRepository;
import com.kthowns.mobidic.api.statistic.service.StatisticService;
import com.kthowns.mobidic.api.user.entity.User;
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
    private WordStatisticRepository wordStatisticRepository;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private StatisticService statisticService;

    private final User testUser = User.builder()
            .id(UUID.randomUUID())
            .build();

    @Test
    @DisplayName("[StatisticService] Get rate by word id success")
    void getRateByWordIdSuccess() {
        resetMock();

        UUID wordId = UUID.randomUUID();

        WordStatistic defaultWordStatistic = WordStatistic.builder()
                .word(mock(Word.class))
                .wordId(wordId)
                .correctCount(3L)
                .incorrectCount(5L)
                .isLearned(true)
                .build();

        //given
        given(wordStatisticRepository.findByWordIdAndWord_Vocabulary_User_Id(any(UUID.class), any(UUID.class)))
                .willReturn(Optional.of(defaultWordStatistic));

        //when
        StatisticDto response = statisticService.getWordStatisticById(testUser, UUID.randomUUID());

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
        given(wordStatisticRepository.getVocabularyLearningRate(any(Vocabulary.class)))
                .willReturn(Optional.of(learningRate));
        given(vocabularyRepository.findByIdAndUser_Id(any(UUID.class), any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(Vocabulary.class)));
        given(wordRepository.countByVocabulary(any(Vocabulary.class)))
                .willReturn(1L);

        //when
        Double foundLearningRate = statisticService.getVocabLearningRate(testUser, vocabularyId);

        //then
        assertEquals(learningRate, foundLearningRate);
    }

    @Test
    @DisplayName("[StatisticService] toggle rate success")
    void toggleRateSuccess() {
        resetMock();

        UUID wordId = UUID.randomUUID();

        WordStatistic defaultStatistic = WordStatistic.builder()
                .wordId(wordId)
                .word(Mockito.mock(Word.class))
                .correctCount(3L)
                .isLearned(true)
                .incorrectCount(4L)
                .build();

        //given
        given(wordRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(Word.class)));
        given(wordStatisticRepository.findForUpdate(any(UUID.class), any(UUID.class)))
                .willReturn(Optional.of(defaultStatistic));
        given(wordStatisticRepository.save(any(WordStatistic.class)))
                .willReturn(Mockito.mock(WordStatistic.class));

        //when
        statisticService.toggleLearnedByWordId(testUser, wordId);

        // then
        assertFalse(defaultStatistic.isLearned());
        verify(wordStatisticRepository, times(1)).findForUpdate(wordId, testUser.getId());
        verify(wordStatisticRepository, never()).save(any());
    }

    private void resetMock() {
        Mockito.reset(wordStatisticRepository, wordRepository);
    }
}
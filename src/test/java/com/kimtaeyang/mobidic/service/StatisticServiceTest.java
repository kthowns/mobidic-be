package com.kimtaeyang.mobidic.service;

import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
import com.kimtaeyang.mobidic.dictionary.entity.Word;
import com.kimtaeyang.mobidic.dictionary.repository.VocabularyRepository;
import com.kimtaeyang.mobidic.dictionary.repository.WordRepository;
import com.kimtaeyang.mobidic.statistic.dto.StatisticDto;
import com.kimtaeyang.mobidic.statistic.entity.Statistic;
import com.kimtaeyang.mobidic.statistic.repository.StatisticRepository;
import com.kimtaeyang.mobidic.statistic.service.StatisticService;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {StatisticService.class, StatisticServiceTest.TestConfig.class})
@ActiveProfiles("dev")
class StatisticServiceTest {
    @Autowired
    private StatisticRepository statisticRepository;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private StatisticService statisticService;

    @Test
    @DisplayName("[RateService] Get rate by word id success")
    void getRateByWordIdSuccess() {
        resetMock();

        UUID wordId = UUID.randomUUID();

        Statistic defaultStatistic = Statistic.builder()
                .word(mock(Word.class))
                .wordId(wordId)
                .correctCount(3)
                .incorrectCount(5)
                .isLearned(1)
                .build();

        //given
        given(wordRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(mock(Word.class)));
        given(statisticRepository.findRateByWord(any(Word.class)))
                .willReturn(Optional.of(defaultStatistic));

        //when
        StatisticDto response = statisticService.getRateByWordId(UUID.randomUUID());

        //then
        assertEquals(defaultStatistic.getWordId(), response.getWordId());
        assertEquals(defaultStatistic.getIsLearned(), response.getIsLearned());
        assertEquals(defaultStatistic.getCorrectCount(), response.getCorrectCount());
        assertEquals(defaultStatistic.getIncorrectCount(), response.getIncorrectCount());
    }

    @Test
    @DisplayName("[RateService] Get vocab learning rate success")
    public void getVocabLearningRateSuccess() {
        resetMock();

        UUID vocabId = UUID.randomUUID();
        Double learningRate = 0.8;

        //given
        given(statisticRepository.getVocabLearningRate(any(Vocabulary.class)))
                .willReturn(Optional.of(learningRate));
        given(vocabularyRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(Vocabulary.class)));
        given(wordRepository.countByVocabulary(any(Vocabulary.class)))
                .willReturn(1L);

        //when
        Double foundLearningRate = statisticService.getVocabLearningRate(vocabId);

        //then
        assertEquals(learningRate, foundLearningRate);
    }

    @Test
    @DisplayName("[RateService] toggle rate success")
    void toggleRateSuccess() {
        resetMock();

        UUID wordId = UUID.randomUUID();

        Statistic defaultRate = Statistic.builder()
                .wordId(wordId)
                .word(Mockito.mock(Word.class))
                .correctCount(3)
                .isLearned(1)
                .incorrectCount(4)
                .build();

        ArgumentCaptor<Statistic> captor =
                ArgumentCaptor.forClass(Statistic.class);

        //given
        given(wordRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(Word.class)));
        given(statisticRepository.findRateByWord(any(Word.class)))
                .willReturn(Optional.of(defaultRate));
        given(statisticRepository.save(any(Statistic.class)))
                .willReturn(Mockito.mock(Statistic.class));

        //when
        statisticService.toggleRateByWordId(wordId);

        //then
        verify(statisticRepository, times(1))
                .save(captor.capture());
        Statistic savedRate = captor.getValue();

        assertEquals(defaultRate.getWordId(), savedRate.getWordId());
        assertEquals(0, savedRate.getIsLearned());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public VocabularyRepository vocabRepository() {
            return Mockito.mock(VocabularyRepository.class);
        }

        @Bean
        public StatisticRepository rateRepository() {
            return Mockito.mock(StatisticRepository.class);
        }

        @Bean
        public WordRepository wordRepository() {
            return Mockito.mock(WordRepository.class);
        }
    }

    private void resetMock() {
        Mockito.reset(statisticRepository, wordRepository);
    }
}
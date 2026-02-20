package com.kimtaeyang.mobidic.service;

import com.kimtaeyang.mobidic.config.ServiceTestConfig;
import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
import com.kimtaeyang.mobidic.dictionary.entity.Word;
import com.kimtaeyang.mobidic.dictionary.repository.VocabularyRepository;
import com.kimtaeyang.mobidic.dictionary.repository.WordRepository;
import com.kimtaeyang.mobidic.statistic.dto.StatisticDto;
import com.kimtaeyang.mobidic.statistic.entity.WordStatistic;
import com.kimtaeyang.mobidic.statistic.repository.WordStatisticRepository;
import com.kimtaeyang.mobidic.statistic.service.StatisticService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {StatisticService.class, ServiceTestConfig.class})
@ActiveProfiles("dev")
class WordStatisticServiceTest {
    @Autowired
    private WordStatisticRepository wordStatisticRepository;

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

        WordStatistic defaultWordStatistic = WordStatistic.builder()
                .word(mock(Word.class))
                .wordId(wordId)
                .correctCount(3)
                .incorrectCount(5)
                .isLearned(true)
                .build();

        //given
        given(wordRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(mock(Word.class)));
        given(wordStatisticRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(defaultWordStatistic));

        //when
        StatisticDto response = statisticService.getRateByWordId(UUID.randomUUID());

        //then
        assertEquals(defaultWordStatistic.getWordId(), response.getWordId());
        assertEquals(defaultWordStatistic.isLearned(), response.isLearned());
        assertEquals(defaultWordStatistic.getCorrectCount(), response.getCorrectCount());
        assertEquals(defaultWordStatistic.getIncorrectCount(), response.getIncorrectCount());
    }

    @Test
    @DisplayName("[RateService] Get vocab learning rate success")
    public void getVocabLearningRateSuccess() {
        resetMock();

        UUID vocabId = UUID.randomUUID();
        Double learningRate = 0.8;

        //given
        given(wordStatisticRepository.getVocabularyLearningRate(any(Vocabulary.class)))
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

        WordStatistic defaultRate = WordStatistic.builder()
                .wordId(wordId)
                .word(Mockito.mock(Word.class))
                .correctCount(3)
                .isLearned(true)
                .incorrectCount(4)
                .build();

        ArgumentCaptor<WordStatistic> captor =
                ArgumentCaptor.forClass(WordStatistic.class);

        //given
        given(wordRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(Mockito.mock(Word.class)));
        given(wordStatisticRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(defaultRate));
        given(wordStatisticRepository.save(any(WordStatistic.class)))
                .willReturn(Mockito.mock(WordStatistic.class));

        //when
        statisticService.toggleLearnedByWordId(wordId);

        //then
        verify(wordStatisticRepository, times(1))
                .save(captor.capture());
        WordStatistic savedRate = captor.getValue();

        assertEquals(defaultRate.getWordId(), savedRate.getWordId());
        assertEquals(true, savedRate.isLearned());
    }

    private void resetMock() {
        Mockito.reset(wordStatisticRepository, wordRepository);
    }
}
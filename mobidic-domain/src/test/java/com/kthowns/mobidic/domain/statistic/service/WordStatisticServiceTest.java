package com.kthowns.mobidic.domain.statistic.service;

import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.domain.vocabulary.service.VocabularyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WordStatisticServiceTest {
    @InjectMocks
    private StatisticService statisticService;

    @Mock
    private StatisticReader statisticReader;

    @Mock
    private StatisticUpdater statisticUpdater;

    @Mock
    private VocabularyService vocabularyService;

    private final UUID userId = UUID.randomUUID();
    private final UUID wordId = UUID.randomUUID();
    private final UUID vocabId = UUID.randomUUID();

    @Test
    @DisplayName("[StatService] Get word statistic by id success")
    void getWordStatisticByIdSuccess() {
        // given
        WordStatistic stat = new WordStatistic(wordId, 3L, 5L, true, 0.6, 0.375);
        given(statisticReader.readByWordIdAndUserId(wordId, userId)).willReturn(stat);

        // when
        WordStatistic response = statisticService.getWordStatisticById(userId, wordId);

        // then
        assertEquals(wordId, response.wordId());
        assertEquals(3L, response.correctCount());
        assertEquals(5L, response.incorrectCount());
        assertEquals(true, response.isLearned());
    }

    @Test
    @DisplayName("[StatService] Get vocab learning rate success")
    void getVocabLearningRateSuccess() {
        // given
        Double learningRate = 0.8;
        given(vocabularyService.existsByIdAndUser(vocabId, userId)).willReturn(true);
        given(statisticReader.readVocabLearningRate(vocabId, userId)).willReturn(learningRate);

        // when
        Double result = statisticService.getVocabLearningRate(userId, vocabId);

        // then
        assertEquals(learningRate, result);
    }

    @Test
    @DisplayName("[StatService] Toggle learned success")
    void toggleLearnedSuccess() {
        // when
        statisticService.toggleLearnedByWordId(userId, wordId);

        // then
        verify(statisticUpdater).toggleLearned(userId, wordId);
    }

    @Test
    @DisplayName("[StatService] Increase correct count success")
    void increaseCorrectCountSuccess() {
        // when
        statisticService.increaseCorrectCount(userId, wordId);

        // then
        verify(statisticUpdater).increaseCorrectCount(userId, wordId);
    }

    @Test
    @DisplayName("[StatService] Increase incorrect count success")
    void increaseIncorrectCountSuccess() {
        // when
        statisticService.increaseIncorrectCount(userId, wordId);

        // then
        verify(statisticUpdater).increaseIncorrectCount(userId, wordId);
    }

    @Test
    @DisplayName("[StatService] Get avg accuracy by vocab success")
    void getAvgAccuracyByVocabSuccess() {
        // given
        List<WordStatistic> wordStatistics = List.of(
                new WordStatistic(UUID.randomUUID(), 10L, 0L, true, 0.1, 1.0),
                new WordStatistic(UUID.randomUUID(), 5L, 5L, true, 0.5, 0.5)
        );
        given(vocabularyService.existsByIdAndUser(vocabId, userId)).willReturn(true);
        given(statisticReader.readByVocabularyId(vocabId, userId)).willReturn(wordStatistics);

        // when
        double result = statisticService.getAvgAccuracyByVocab(userId, vocabId);

        // then
        assertEquals(0.75, result);
    }

    @Test
    @DisplayName("[StatService] Get total avg accuracy success")
    void getTotalAvgAccuracySuccess() {
        // given
        List<WordStatistic> wordStatistics = List.of(
                new WordStatistic(UUID.randomUUID(), 10L, 0L, true, 0.1, 1.0),
                new WordStatistic(UUID.randomUUID(), 5L, 5L, true, 0.5, 0.5)
        );
        given(statisticReader.readByUserId(userId)).willReturn(wordStatistics);

        // when
        double result = statisticService.getTotalAvgAccuracy(userId);

        // then
        assertEquals(0.75, result);
    }
}

package com.kthowns.mobidic.domain.statistic.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.domain.vocabulary.service.VocabularyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticService {
    private final StatisticReader statisticReader;
    private final StatisticUpdater statisticUpdater;
    private final StatisticAppender statisticAppender;
    private final VocabularyService vocabularyService;

    @Transactional
    public void append(UUID wordId) {
        statisticAppender.append(wordId);
    }

    @Transactional(readOnly = true)
    public WordStatistic getWordStatisticById(UUID userId, UUID wordId) {
        return statisticReader.readByWordIdAndUserId(wordId, userId);
    }

    @Transactional(readOnly = true)
    public double getVocabLearningRate(UUID userId, UUID vocabId) {
        if (!vocabularyService.existsByIdAndUser(vocabId, userId)) {
            throw new ApiException(GeneralResponseCode.NO_VOCAB);
        }

        return statisticReader.readVocabLearningRate(vocabId, userId);
    }

    @Transactional
    public void toggleLearnedByWordId(UUID userId, UUID wordId) {
        statisticUpdater.toggleLearned(userId, wordId);
    }

    @Transactional
    public void increaseCorrectCount(UUID userId, UUID wordId) {
        statisticUpdater.increaseCorrectCount(userId, wordId);
    }

    @Transactional
    public void increaseIncorrectCount(UUID userId, UUID wordId) {
        statisticUpdater.increaseIncorrectCount(userId, wordId);
    }

    @Transactional(readOnly = true)
    public double getAvgAccuracyByVocab(UUID userId, UUID vocabularyId) {
        if (!vocabularyService.existsByIdAndUser(vocabularyId, userId)) {
            throw new ApiException(GeneralResponseCode.NO_VOCAB);
        }

        List<WordStatistic> wordStatistics = statisticReader.readByVocabularyId(vocabularyId, userId);
        return calcAvgRate(wordStatistics);
    }

    @Transactional(readOnly = true)
    public double getTotalAvgAccuracy(UUID userId) {
        List<WordStatistic> wordStatistics = statisticReader.readByUserId(userId);
        return calcAvgRate(wordStatistics);
    }

    private double calcAvgRate(List<WordStatistic> wordStatistics) {
        if (wordStatistics == null || wordStatistics.isEmpty()) {
            return 0.0;
        }

        return wordStatistics.stream()
                .mapToDouble(ws -> WordStatistic.calculateAverageAccuracy(ws.correctCount(), ws.incorrectCount()))
                .average()
                .orElse(0.0);
    }
}

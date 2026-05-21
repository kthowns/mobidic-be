package com.kthowns.mobidic.domain.statistic.service;

import com.kthowns.mobidic.domain.statistic.implementation.StatisticCalculator;
import com.kthowns.mobidic.domain.statistic.implementation.StatisticReader;
import com.kthowns.mobidic.domain.statistic.implementation.StatisticUpdater;
import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.domain.vocabulary.implementation.VocabularyReader;
import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
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
    private final StatisticCalculator statisticCalculator;
    private final VocabularyReader vocabularyReader;

    @Transactional(readOnly = true)
    public WordStatistic getWordStatisticById(UUID userId, UUID wordId) {
        return statisticReader.readByWordIdAndUserId(wordId, userId);
    }

    @Transactional(readOnly = true)
    public Double getVocabLearningRate(UUID userId, UUID vocabId) {
        if (!vocabularyReader.existsByIdAndUser(vocabId, userId)) {
            throw new ApiException(GeneralResponseCode.NO_VOCAB);
        }

        return statisticReader.readVocabLearningRate(vocabId, userId);
    }

    @Transactional
    public void toggleLearnedByWordId(UUID userId, UUID wordId) {
        WordStatistic wordStatistic = statisticReader.readForUpdate(wordId, userId);
        
        statisticUpdater.update(
                userId, 
                wordId, 
                wordStatistic.getCorrectCount(), 
                wordStatistic.getIncorrectCount(), 
                !wordStatistic.isLearned()
        );
    }

    @Transactional
    public void increaseCorrectCount(UUID userId, UUID wordId) {
        WordStatistic wordStatistic = statisticReader.readForUpdate(wordId, userId);
        
        statisticUpdater.update(
                userId,
                wordId,
                wordStatistic.getCorrectCount() + 1,
                wordStatistic.getIncorrectCount(),
                wordStatistic.isLearned()
        );
    }

    @Transactional
    public void increaseIncorrectCount(UUID userId, UUID wordId) {
        WordStatistic wordStatistic = statisticReader.readForUpdate(wordId, userId);
        
        statisticUpdater.update(
                userId,
                wordId,
                wordStatistic.getCorrectCount(),
                wordStatistic.getIncorrectCount() + 1,
                wordStatistic.isLearned()
        );
    }

    @Transactional(readOnly = true)
    public double getAvgAccuracyByVocab(UUID userId, UUID vocabularyId) {
        if (!vocabularyReader.existsByIdAndUser(vocabularyId, userId)) {
            throw new ApiException(GeneralResponseCode.NO_VOCAB);
        }

        List<WordStatistic> wordStatistics = statisticReader.readByVocabularyId(vocabularyId);
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
                .mapToDouble(ws -> statisticCalculator.calculateAverageAccuracy(ws.getCorrectCount(), ws.getIncorrectCount()))
                .average()
                .orElse(0.0);
    }
}

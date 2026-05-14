package com.kthowns.mobidic.api.statistic.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.api.dictionary.entity.Vocabulary;
import com.kthowns.mobidic.api.dictionary.repository.VocabularyRepository;
import com.kthowns.mobidic.api.dictionary.repository.WordRepository;
import com.kthowns.mobidic.api.dto.common.statistic.StatisticDto;
import com.kthowns.mobidic.api.statistic.entity.WordStatistic;
import com.kthowns.mobidic.api.statistic.repository.WordStatisticRepository;
import com.kthowns.mobidic.api.statistic.util.DifficultyUtil;
import com.kthowns.mobidic.api.user.entity.User;
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
    private final WordRepository wordRepository;
    private final WordStatisticRepository wordStatisticRepository;
    private final VocabularyRepository vocabularyRepository;
    private final DifficultyUtil difficultyUtil;

    @Transactional(readOnly = true)
    public StatisticDto getWordStatisticById(User user, UUID wordId) {
        WordStatistic wordStatistic = wordStatisticRepository
                .findByWordIdAndWord_Vocabulary_User_Id(wordId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_STAT));

        return StatisticDto.fromEntity(wordStatistic);
    }

    @Transactional(readOnly = true)
    public Double getVocabLearningRate(User user, UUID vocabId) {
        Vocabulary vocabulary = vocabularyRepository
                .findByIdAndUser_Id(vocabId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));

        if (wordRepository.countByVocabulary(vocabulary) < 1) {
            return 0.0;
        }

        return wordStatisticRepository.getVocabularyLearningRate(vocabulary)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.INTERNAL_SERVER_ERROR));
    }

    @Transactional
    public void toggleLearnedByWordId(User user, UUID wordId) {
        WordStatistic wordStatistic = wordStatisticRepository
                .findForUpdate(wordId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_STAT));

        wordStatistic.toggleIsLearned();
    }

    @Transactional
    public void increaseCorrectCount(User user, UUID wordId) {
        WordStatistic wordStatistic = wordStatisticRepository
                .findForUpdate(wordId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_STAT));

        wordStatistic.increaseCorrectCount();
        setDifficultyAndAccuracy(wordStatistic);
    }

    @Transactional
    public void increaseIncorrectCount(User user, UUID wordId) {
        WordStatistic wordStatistic = wordStatisticRepository
                .findForUpdate(wordId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_STAT));

        wordStatistic.increaseIncorrectCount();
        setDifficultyAndAccuracy(wordStatistic);
    }

    @Transactional(readOnly = true)
    public double getAvgAccuracyByVocab(User user, UUID vocabularyId) {
        vocabularyRepository.findByIdAndUser_Id(vocabularyId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));

        List<WordStatistic> wordStatistics = wordStatisticRepository.findByWord_Vocabulary_Id(vocabularyId);

        return calcAvgRate(wordStatistics);
    }

    @Transactional(readOnly = true)
    public double getTotalAvgAccuracy(User user) {
        List<WordStatistic> wordStatistics = wordStatisticRepository.findByWord_Vocabulary_User_Id(user.getId());

        return calcAvgRate(wordStatistics);
    }

    private double calcAvgRate(List<WordStatistic> wordStatistics) {
        if (wordStatistics == null || wordStatistics.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        for (WordStatistic wordStatistic : wordStatistics) {
            if (wordStatistic.getIncorrectCount() == 0) {
                if (wordStatistic.getCorrectCount() > 0) {
                    sum += 1;
                }
            } else {
                sum += (double) wordStatistic.getCorrectCount() / (wordStatistic.getIncorrectCount() + wordStatistic.getCorrectCount());
            }
        }

        return sum / wordStatistics.size();
    }

    private void setDifficultyAndAccuracy(WordStatistic wordStatistic) {
        wordStatistic.setDifficulty(difficultyUtil.calcDifficultyRatio(
                wordStatistic.getCorrectCount(), wordStatistic.getIncorrectCount()
        ));
        wordStatistic.setAccuracy(difficultyUtil.calcAccuracyRatio(
                wordStatistic.getCorrectCount(), wordStatistic.getIncorrectCount()
        ));
    }
}

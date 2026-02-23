package com.kimtaeyang.mobidic.statistic.service;

import com.kimtaeyang.mobidic.common.code.GeneralResponseCode;
import com.kimtaeyang.mobidic.common.exception.ApiException;
import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
import com.kimtaeyang.mobidic.dictionary.entity.Word;
import com.kimtaeyang.mobidic.dictionary.repository.VocabularyRepository;
import com.kimtaeyang.mobidic.dictionary.repository.WordRepository;
import com.kimtaeyang.mobidic.statistic.dto.StatisticDto;
import com.kimtaeyang.mobidic.statistic.entity.WordStatistic;
import com.kimtaeyang.mobidic.statistic.repository.WordStatisticRepository;
import com.kimtaeyang.mobidic.user.entity.User;
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

    @Transactional(readOnly = true)
    public StatisticDto getRateByWordId(User user, UUID wordId) {
        WordStatistic wordStatistic = wordStatisticRepository
                .findByWordIdAndWord_Vocabulary_User_Id(wordId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_RATE));

        return StatisticDto.fromEntity(wordStatistic, calcDifficultyRatio(wordStatistic.getCorrectCount(), wordStatistic.getIncorrectCount()));
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
                .findByWordIdAndWord_Vocabulary_User_Id(wordId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_RATE));

        wordStatistic.setLearned(!wordStatistic.isLearned());
    }

    @Transactional
    public void increaseCorrectCount(User user, UUID wordId) {
        Word word = wordRepository
                .findByIdAndVocabulary_User_Id(wordId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));
        wordStatisticRepository.increaseCorrectCount(word);
    }

    @Transactional
    public void increaseIncorrectCount(User user, UUID wordId) {
        Word word = wordRepository
                .findByIdAndVocabulary_User_Id(wordId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));
        wordStatisticRepository.increaseIncorrectCount(word);
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

    private double calcDifficultyRatio(Integer correct, Integer incorrect) {
        /*
            난이도 함수 : -0.04correct + 0.05incorrect + 0.5
        */
        double diff = (-0.04 * correct) + (0.05 * incorrect) + 0.5;
        if (diff > 1) {
            diff = 1;
        } else if (diff < 0) {
            diff = 0;
        }

        return diff;
    }
}

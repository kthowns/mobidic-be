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
import com.kimtaeyang.mobidic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.kimtaeyang.mobidic.common.code.AuthResponseCode.NO_USER;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticService {
    private final WordRepository wordRepository;
    private final WordStatisticRepository wordStatisticRepository;
    private final UserRepository userRepository;
    private final VocabularyRepository vocabularyRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("@statisticAccessHandler.ownershipCheck(#wordId)")
    public StatisticDto getRateByWordId(UUID wordId) {
        WordStatistic wordStatistic = wordStatisticRepository.findById(wordId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_RATE));

        return StatisticDto.fromEntity(wordStatistic, calcDifficultyRatio(wordStatistic.getCorrectCount(), wordStatistic.getIncorrectCount()));
    }

    @PreAuthorize("@vocabularyAccessHandler.ownershipCheck(#vocabId)")
    @Transactional(readOnly = true)
    public Double getVocabLearningRate(UUID vocabId) {
        Vocabulary vocabulary = vocabularyRepository.findById(vocabId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));
        if(wordRepository.countByVocabulary(vocabulary) < 1){
            return 0.0;
        }
        return wordStatisticRepository.getVocabularyLearningRate(vocabulary)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.INTERNAL_SERVER_ERROR));
    }

    @Transactional
    @PreAuthorize("@wordAccessHandler.ownershipCheck(#wordId)")
    public void toggleLearnedByWordId(UUID wordId) {
        WordStatistic wordStatistic = wordStatisticRepository.findById(wordId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_RATE));

        wordStatistic.setLearned(!wordStatistic.isLearned());
    }

    @Transactional
    @PreAuthorize("@wordAccessHandler.ownershipCheck(#wordId)")
    public void increaseCorrectCount(UUID wordId) {
        Word word = wordRepository.findById(wordId)
                        .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));
        wordStatisticRepository.increaseCorrectCount(word);
    }

    @Transactional
    @PreAuthorize("@wordAccessHandler.ownershipCheck(#wordId)")
    public void increaseIncorrectCount(UUID wordId) {
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));
        wordStatisticRepository.increaseIncorrectCount(word);
    }

    @Transactional
    @PreAuthorize("@vocabularyAccessHandler.ownershipCheck(#vId)")
    public double getAvgAccuracyByVocab(UUID vId) {
        Vocabulary vocabulary = vocabularyRepository.findById(vId)
                .orElseThrow(()-> new ApiException(GeneralResponseCode.NO_VOCAB));

        List<WordStatistic> wordStatistics = wordStatisticRepository.findByVocab(vocabulary);

        return calcAvgRate(wordStatistics);
    }

    @Transactional
    @PreAuthorize("@userAccessHandler.ownershipCheck(#uId)")
    public double getAvgAccuracyByMember(UUID uId) {
        User user = userRepository.findById(uId)
                .orElseThrow(()-> new ApiException(NO_USER));

        List<WordStatistic> wordStatistics = wordStatisticRepository.findByMember(user);

        return calcAvgRate(wordStatistics);
    }

    private double calcAvgRate(List<WordStatistic> wordStatistics) {
        if(wordStatistics == null || wordStatistics.isEmpty()){
            return 0.0;
        }

        double sum = 0.0;
        for(WordStatistic wordStatistic : wordStatistics){
            if(wordStatistic.getIncorrectCount() == 0){
                if(wordStatistic.getCorrectCount() > 0){
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

package com.kimtaeyang.mobidic.statistic.service;

import com.kimtaeyang.mobidic.common.code.GeneralResponseCode;
import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
import com.kimtaeyang.mobidic.statistic.dto.StatisticDto;
import com.kimtaeyang.mobidic.user.entity.User;
import com.kimtaeyang.mobidic.statistic.entity.Statistic;
import com.kimtaeyang.mobidic.dictionary.entity.Word;
import com.kimtaeyang.mobidic.common.exception.ApiException;
import com.kimtaeyang.mobidic.user.repository.UserRepository;
import com.kimtaeyang.mobidic.statistic.repository.StatisticRepository;
import com.kimtaeyang.mobidic.dictionary.repository.VocabularyRepository;
import com.kimtaeyang.mobidic.dictionary.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.kimtaeyang.mobidic.common.code.AuthResponseCode.NO_MEMBER;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticService {
    private final WordRepository wordRepository;
    private final StatisticRepository statisticRepository;
    private final UserRepository userRepository;
    private final VocabularyRepository vocabularyRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("@rateAccessHandler.ownershipCheck(#wordId)")
    public StatisticDto getRateByWordId(UUID wordId) {
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));
        Statistic statistic = statisticRepository.findRateByWord(word)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_RATE));

        return StatisticDto.fromEntity(statistic, calcDifficultyRatio(statistic.getCorrectCount(), statistic.getIncorrectCount()));
    }

    @PreAuthorize("@vocabAccessHandler.ownershipCheck(#vocabId)")
    @Transactional(readOnly = true)
    public Double getVocabLearningRate(UUID vocabId) {
        Vocabulary vocabulary = vocabularyRepository.findById(vocabId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));
        if(wordRepository.countByVocabulary(vocabulary) < 1){
            return 0.0;
        }
        return statisticRepository.getVocabLearningRate(vocabulary)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.INTERNAL_SERVER_ERROR));
    }

    @Transactional
    @PreAuthorize("@wordAccessHandler.ownershipCheck(#wordId)")
    public void toggleRateByWordId(UUID wordId) {
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));
        Statistic statistic = statisticRepository.findRateByWord(word)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_RATE));

        if(statistic.getIsLearned() > 0){
            statistic.setIsLearned(0);
        } else {
            statistic.setIsLearned(1);
        }

        statisticRepository.save(statistic);
    }

    @Transactional
    @PreAuthorize("@wordAccessHandler.ownershipCheck(#wordId)")
    public void increaseCorrectCount(UUID wordId) {
        Word word = wordRepository.findById(wordId)
                        .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));
        statisticRepository.increaseCorrectCount(word);
    }

    @Transactional
    @PreAuthorize("@wordAccessHandler.ownershipCheck(#wordId)")
    public void increaseIncorrectCount(UUID wordId) {
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));
        statisticRepository.increaseIncorrectCount(word);
    }

    @Transactional
    @PreAuthorize("@vocabAccessHandler.ownershipCheck(#vId)")
    public double getAvgAccuracyByVocab(UUID vId) {
        Vocabulary vocabulary = vocabularyRepository.findById(vId)
                .orElseThrow(()-> new ApiException(GeneralResponseCode.NO_VOCAB));

        List<Statistic> statistics = statisticRepository.findByVocab(vocabulary);

        return calcAvgRate(statistics);
    }

    @Transactional
    @PreAuthorize("@userAccessHandler.ownershipCheck(#uId)")
    public double getAvgAccuracyByMember(UUID uId) {
        User user = userRepository.findById(uId)
                .orElseThrow(()-> new ApiException(NO_MEMBER));

        List<Statistic> statistics = statisticRepository.findByMember(user);

        return calcAvgRate(statistics);
    }

    private double calcAvgRate(List<Statistic> statistics) {
        if(statistics == null || statistics.isEmpty()){
            return 0.0;
        }

        double sum = 0.0;
        for(Statistic statistic : statistics){
            if(statistic.getIncorrectCount() == 0){
                if(statistic.getCorrectCount() > 0){
                    sum += 1;
                }
            } else {
                sum += (double) statistic.getCorrectCount() / (statistic.getIncorrectCount() + statistic.getCorrectCount());
            }
        }

        return sum / statistics.size();
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

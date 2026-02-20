package com.kimtaeyang.mobidic.security.accesshandler;

import com.kimtaeyang.mobidic.statistic.entity.WordStatistic;
import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
import com.kimtaeyang.mobidic.dictionary.entity.Word;
import com.kimtaeyang.mobidic.statistic.repository.WordStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticAccessHandler extends AccessHandler {
    private final WordStatisticRepository wordStatisticRepository;

    @Override
    boolean isResourceOwner(UUID resourceId) {
        return wordStatisticRepository.findById(resourceId)
                .map(WordStatistic::getWord)
                .map(Word::getVocabulary)
                .map(Vocabulary::getUser)
                .filter((m) -> getCurrentUserId().equals(m.getId()))
                .isPresent();
    }
}

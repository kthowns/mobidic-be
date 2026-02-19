package com.kimtaeyang.mobidic.security.accesshandler;

import com.kimtaeyang.mobidic.statistic.entity.Statistic;
import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
import com.kimtaeyang.mobidic.dictionary.entity.Word;
import com.kimtaeyang.mobidic.statistic.repository.StatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RateAccessHandler extends AccessHandler {
    private final StatisticRepository statisticRepository;

    @Override
    boolean isResourceOwner(UUID resourceId) {
        return statisticRepository.findById(resourceId)
                .map(Statistic::getWord)
                .map(Word::getVocabulary)
                .map(Vocabulary::getUser)
                .filter((m) -> getCurrentMemberId().equals(m.getId()))
                .isPresent();
    }
}

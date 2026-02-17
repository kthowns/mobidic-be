package com.kimtaeyang.mobidic.security.accesshandler;

import com.kimtaeyang.mobidic.entity.Rate;
import com.kimtaeyang.mobidic.entity.Vocab;
import com.kimtaeyang.mobidic.entity.Word;
import com.kimtaeyang.mobidic.repository.RateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RateAccessHandler extends AccessHandler {
    private final RateRepository rateRepository;

    @Override
    boolean isResourceOwner(UUID resourceId) {
        return rateRepository.findById(resourceId)
                .map(Rate::getWord)
                .map(Word::getVocab)
                .map(Vocab::getMember)
                .filter((m) -> getCurrentMemberId().equals(m.getId()))
                .isPresent();
    }
}

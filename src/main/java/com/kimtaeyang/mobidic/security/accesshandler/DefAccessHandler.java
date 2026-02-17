package com.kimtaeyang.mobidic.security.accesshandler;

import com.kimtaeyang.mobidic.entity.Def;
import com.kimtaeyang.mobidic.entity.Vocab;
import com.kimtaeyang.mobidic.entity.Word;
import com.kimtaeyang.mobidic.repository.DefRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefAccessHandler extends AccessHandler {
    private final DefRepository defRepository;

    @Override
    boolean isResourceOwner(UUID resourceId) {
        return defRepository.findById(resourceId)
                .map(Def::getWord)
                .map(Word::getVocab)
                .map(Vocab::getMember)
                .filter((m) -> getCurrentMemberId().equals(m.getId()))
                .isPresent();
    }
}

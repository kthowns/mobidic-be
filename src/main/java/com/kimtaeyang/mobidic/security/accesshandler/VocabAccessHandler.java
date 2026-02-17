package com.kimtaeyang.mobidic.security.accesshandler;

import com.kimtaeyang.mobidic.entity.Vocab;
import com.kimtaeyang.mobidic.repository.VocabRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VocabAccessHandler extends AccessHandler {
    private final VocabRepository vocabRepository;

    @Override
    boolean isResourceOwner(UUID resourceId) {
        return vocabRepository.findById(resourceId)
                .map(Vocab::getMember)
                .filter((m) -> getCurrentMemberId().equals(m.getId()))
                .isPresent();
    }
}

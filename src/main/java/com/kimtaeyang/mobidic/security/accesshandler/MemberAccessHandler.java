package com.kimtaeyang.mobidic.security.accesshandler;

import com.kimtaeyang.mobidic.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberAccessHandler extends AccessHandler {
    private final MemberRepository memberRepository;

    @Override
    boolean isResourceOwner(UUID resourceId) {
        return memberRepository.findById(resourceId)
                .filter((m) -> getCurrentMemberId().equals(resourceId))
                .isPresent();
    }
}

package com.kthowns.mobidic.domain.term.implementation;

import com.kthowns.mobidic.domain.term.repository.UserAgreementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserAgreementAppender {
    private final UserAgreementRepository userAgreementRepository;

    public void appendAgreements(UUID userId, List<Long> agreeTermIds) {
        userAgreementRepository.saveAgreements(userId, agreeTermIds);
    }
}

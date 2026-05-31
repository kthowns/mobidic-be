package com.kthowns.mobidic.domain.term.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.term.model.TermType;
import com.kthowns.mobidic.domain.term.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
class TermValidator {
    private final TermRepository termRepository;

    public void validateVersionDuplication(TermType type, String version) {
        if (termRepository.existsByTypeAndVersion(type, version)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_TERM_VERSION);
        }
    }

    public void validateAgreement(List<Long> agreeTermIds) {
        List<Long> requiredIds = termRepository.readAllRequiredTermIds();
        Set<Long> agreedSet = new HashSet<>(agreeTermIds);

        for (Long requiredId : requiredIds) {
            if (!agreedSet.contains(requiredId)) {
                throw new ApiException(GeneralResponseCode.REQUIRED_TERM_NOT_AGREED);
            }
        }

        if (termRepository.countByIds(agreeTermIds) != agreeTermIds.size()) {
            throw new ApiException(GeneralResponseCode.INVALID_TERM_ID_INCLUDED);
        }
    }
}

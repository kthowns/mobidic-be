package com.kthowns.mobidic.term.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.term.dto.AddTermRequest;
import com.kthowns.mobidic.term.dto.TermDto;
import com.kthowns.mobidic.term.dto.TermSimpleDto;
import com.kthowns.mobidic.term.entity.Term;
import com.kthowns.mobidic.term.entity.UserAgreement;
import com.kthowns.mobidic.term.repository.TermRepository;
import com.kthowns.mobidic.term.repository.UserAgreementRepository;
import com.kthowns.mobidic.term.type.TermType;
import com.kthowns.mobidic.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermService {

    private final TermRepository termRepository;
    private final UserAgreementRepository userAgreementRepository;

    @Transactional(readOnly = true)
    public TermDto getTerm(TermType type, String version) {
        if (version == null || version.isEmpty()) {
            Term term = termRepository.findFirstByTypeAndActiveTrueOrderByCreatedAtDesc(type)
                    .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_TERM));

            return TermDto.fromEntity(term);
        }

        Term term = termRepository.findByTypeAndVersion(type, version)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_TERM));

        return TermDto.fromEntity(term);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void addTerm(AddTermRequest addTermRequest) {
        boolean isVersionDuplicated = termRepository.existsByTypeAndVersion(addTermRequest.getType(), addTermRequest.getVersion());

        if (isVersionDuplicated) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_TERM_VERSION);
        }

        termRepository.deactivateAllByType(addTermRequest.getType());

        Term term = Term.builder()
                .type(addTermRequest.getType())
                .version(addTermRequest.getVersion())
                .required(addTermRequest.isRequired())
                .content(addTermRequest.getContent())
                .build();

        termRepository.save(term);
    }

    @Transactional(readOnly = true)
    public void validateSignUpAgreement(List<Long> agreeTermIds) {
        // term.isRequired, term.isActive가 true인 모든 Term의 Id가 포함되었는지 검사
        List<Long> requiredIds = termRepository.findAllRequiredTermIds();

        Set<Long> agreedSet = new HashSet<>(agreeTermIds);

        for (Long requiredId : requiredIds) {
            if (!agreedSet.contains(requiredId)) {
                // 필수 약관 중 하나라도 동의하지 않음
                throw new ApiException(GeneralResponseCode.REQUIRED_TERM_NOT_AGREED);
            }
        }

        // terms 테이블에 없는 id가 포함되었는지 검사
        long validCount = termRepository.countByIdIn(agreeTermIds);
        if (validCount != agreeTermIds.size()) {
            throw new ApiException(GeneralResponseCode.INVALID_TERM_ID_INCLUDED);
        }
    }

    @Transactional
    public void addUserAgreement(User user, List<Long> agreeTermIds) {
        List<Term> terms = termRepository.findByIdIn(agreeTermIds);
        List<UserAgreement> userAgreements = terms.stream()
                .map(term ->
                        UserAgreement.builder()
                                .term(term)
                                .user(user)
                                .build()
                ).toList();

        userAgreementRepository.saveAll(userAgreements);
    }

    @Transactional(readOnly = true)
    public List<TermSimpleDto> getActiveTerms() {
        List<Term> terms = termRepository.findAllByActiveTrue();

        return terms.stream().map(TermSimpleDto::fromEntity).toList();
    }
}

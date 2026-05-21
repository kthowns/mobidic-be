package com.kthowns.mobidic.domain.term.service;

import com.kthowns.mobidic.domain.term.implementation.TermAppender;
import com.kthowns.mobidic.domain.term.implementation.TermReader;
import com.kthowns.mobidic.domain.term.implementation.TermValidator;
import com.kthowns.mobidic.domain.term.implementation.UserAgreementAppender;
import com.kthowns.mobidic.domain.term.model.SimpleTerm;
import com.kthowns.mobidic.domain.term.model.Term;
import com.kthowns.mobidic.domain.term.model.TermType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TermService {
    private final TermReader termReader;
    private final TermAppender termAppender;
    private final TermValidator termValidator;
    private final UserAgreementAppender userAgreementAppender;

    @Transactional(readOnly = true)
    public Term getTerm(TermType type, String version) {
        return termReader.readTerm(type, version);
    }

    @Transactional
    public void addTerm(TermType type, String version, boolean required, String content) {
        termValidator.validateVersionDuplication(type, version);

        Term term = Term.builder()
                .type(type)
                .version(version)
                .required(required)
                .content(content)
                .build();

        termAppender.append(term);
    }

    @Transactional(readOnly = true)
    public void validateSignUpAgreement(List<Long> agreeTermIds) {
        termValidator.validateAgreement(agreeTermIds);
    }

    @Transactional
    public void addUserAgreement(UUID userId, List<Long> agreeTermIds) {
        userAgreementAppender.appendAgreements(userId, agreeTermIds);
    }

    @Transactional(readOnly = true)
    public List<SimpleTerm> getActiveTerms() {
        return termReader.readActiveTerms();
    }
}

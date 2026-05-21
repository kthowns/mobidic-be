package com.kthowns.mobidic.domain.term.implementation;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.term.model.SimpleTerm;
import com.kthowns.mobidic.domain.term.model.Term;
import com.kthowns.mobidic.domain.term.model.TermType;
import com.kthowns.mobidic.domain.term.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TermReader {
    private final TermRepository termRepository;

    public Term readTerm(TermType type, String version) {
        if (version == null || version.isEmpty()) {
            return termRepository.readLatestByType(type)
                    .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_TERM));
        }

        return termRepository.readByTypeAndVersion(type, version)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_TERM));
    }

    public List<SimpleTerm> readActiveTerms() {
        return termRepository.readActiveTerms();
    }
}

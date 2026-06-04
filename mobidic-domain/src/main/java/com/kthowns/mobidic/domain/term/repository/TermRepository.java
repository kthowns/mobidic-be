package com.kthowns.mobidic.domain.term.repository;

import com.kthowns.mobidic.domain.term.model.SimpleTerm;
import com.kthowns.mobidic.domain.term.model.Term;
import com.kthowns.mobidic.domain.term.model.TermType;

import java.util.List;
import java.util.Optional;

public interface TermRepository {
    Optional<Term> readLatestByType(TermType type);

    Optional<Term> readByTypeAndVersion(TermType type, String version);

    void deactivateAllByType(TermType type);

    void append(Term term);

    List<Long> readAllRequiredTermIds();

    long countByIds(List<Long> ids);

    List<SimpleTerm> readActiveTerms();

    boolean existsByTypeAndVersion(TermType type, String version);
}

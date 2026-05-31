package com.kthowns.mobidic.domain.term.service;

import com.kthowns.mobidic.domain.term.model.Term;
import com.kthowns.mobidic.domain.term.model.TermType;
import com.kthowns.mobidic.domain.term.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class TermAppender {
    private final TermRepository termRepository;

    public void append(TermType type, String version, boolean required, String content) {
        termRepository.deactivateAllByType(type);

        termRepository.append(Term.create(type, version, required, content));
    }
}

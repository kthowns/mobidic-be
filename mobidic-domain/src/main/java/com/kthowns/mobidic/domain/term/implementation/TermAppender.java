package com.kthowns.mobidic.domain.term.implementation;

import com.kthowns.mobidic.domain.term.model.Term;
import com.kthowns.mobidic.domain.term.model.TermType;
import com.kthowns.mobidic.domain.term.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TermAppender {
    private final TermRepository termRepository;

    public void append(Term term) {
        termRepository.deactivateAllByType(term.getType());
        termRepository.append(term);
    }
}

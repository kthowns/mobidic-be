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

    public void append(TermType type, String version, boolean required, String content) {
        termRepository.deactivateAllByType(type);
        
        Term term = Term.builder()
                .type(type)
                .version(version)
                .required(required)
                .content(content)
                .build();
        termRepository.append(term);
    }
}

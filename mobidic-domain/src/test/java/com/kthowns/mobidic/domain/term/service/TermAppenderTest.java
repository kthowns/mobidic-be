package com.kthowns.mobidic.domain.term.service;

import com.kthowns.mobidic.domain.term.model.Term;
import com.kthowns.mobidic.domain.term.model.TermType;
import com.kthowns.mobidic.domain.term.repository.TermRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TermAppenderTest {

    @Mock
    private TermRepository termRepository;

    @InjectMocks
    private TermAppender termAppender;

    @Test
    @DisplayName("append 테스트 - 약관 저장 성공")
    void appendTest() {
        // Given
        TermType type = TermType.SERVICE;
        String version = "1.0.0";
        boolean required = true;
        String content = "약관 내용";

        // When
        termAppender.append(type, version, required, content);

        // Then
        verify(termRepository).deactivateAllByType(type);

        ArgumentCaptor<Term> captor = ArgumentCaptor.forClass(Term.class);
        verify(termRepository).append(captor.capture());

        Term capturedTerm = captor.getValue();
        assertThat(capturedTerm.type()).isEqualTo(type);
        assertThat(capturedTerm.version()).isEqualTo(version);
        assertThat(capturedTerm.required()).isEqualTo(required);
        assertThat(capturedTerm.content()).isEqualTo(content);
    }
}

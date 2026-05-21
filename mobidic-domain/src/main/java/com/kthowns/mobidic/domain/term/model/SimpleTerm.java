package com.kthowns.mobidic.domain.term.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleTerm {
    private Long id;
    private TermType type;
    private String version;
    private boolean required;
    private String contentUri;
    private LocalDateTime createdAt;
}

package com.kthowns.mobidic.api.term.dto.common;

import com.kthowns.mobidic.storage.term.jpaentity.Term;
import com.kthowns.mobidic.domain.term.model.TermType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TermSimpleDto {
    private Long id;
    private TermType type;
    private String version;
    private boolean required;
    private String contentUri;
    private LocalDateTime createdAt;

    public static TermSimpleDto fromEntity(Term term) {
        return TermSimpleDto.builder()
                .id(term.getId())
                .type(term.getType())
                .version(term.getVersion())
                .required(term.isRequired())
                .createdAt(term.getCreatedAt())
                .contentUri(String.format("/terms/%s",
                        term.getType().name().toLowerCase()))
                .build();
    }
}

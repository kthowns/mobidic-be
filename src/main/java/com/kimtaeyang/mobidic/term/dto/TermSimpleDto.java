package com.kimtaeyang.mobidic.term.dto;

import com.kimtaeyang.mobidic.term.entity.Term;
import com.kimtaeyang.mobidic.term.type.TermType;
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

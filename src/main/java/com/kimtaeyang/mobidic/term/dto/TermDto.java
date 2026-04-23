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
public class TermDto {
    private Long id;
    private TermType type;
    private String version;
    private boolean required;
    private String content;
    private LocalDateTime createdAt;

    public static TermDto fromEntity(Term term) {
        return TermDto.builder()
                .id(term.getId())
                .type(term.getType())
                .version(term.getVersion())
                .content(term.getContent())
                .required(term.isRequired())
                .createdAt(term.getCreatedAt())
                .build();
    }
}


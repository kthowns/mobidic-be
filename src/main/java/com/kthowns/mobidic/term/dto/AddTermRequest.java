package com.kthowns.mobidic.term.dto;

import com.kthowns.mobidic.term.type.TermType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AddTermRequest {
    @NotNull
    private TermType type;

    @NotBlank
    private String version;

    @NotNull
    private boolean required;

    @NotBlank(message = "약관 내용은 필수입니다.")
    private String content;
}

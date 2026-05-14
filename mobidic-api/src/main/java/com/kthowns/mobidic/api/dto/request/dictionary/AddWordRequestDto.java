package com.kthowns.mobidic.api.dto.request.dictionary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddWordRequestDto {
    @NotBlank
    @Size(min = 1, max = 45, message = "단어는 45자 미만이어야 합니다.")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z ]*$", message = "단어는 영문자여야 합니다.")
    private String expression;
}
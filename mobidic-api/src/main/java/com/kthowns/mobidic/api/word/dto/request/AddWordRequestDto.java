package com.kthowns.mobidic.api.word.dto.request;

import com.kthowns.mobidic.api.definition.dto.request.AddDefinitionRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddWordRequestDto {
    @NotBlank
    @Size(min = 1, max = 45, message = "단어는 45자 이하여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z ]*$", message = "단어는 영문자여야 합니다.")
    private String expression;

    @Valid
    @Size(min = 1, max = 10, message = "뜻은 최소 1개, 10개 이하여야 합니다.")
    private List<AddDefinitionRequestDto> definitions;
}
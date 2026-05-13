package com.kthowns.mobidic.dictionary.dto;

import com.kthowns.mobidic.dictionary.type.PartOfSpeech;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddDefinitionRequestDto {
    @NotBlank
    @Size(max = 32, message = "32자 미만이어야 합니다.")
    private String meaning;
    @NotNull(message = "품사는 필수 입력값 입니다.")
    private PartOfSpeech part;
}
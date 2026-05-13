package com.kthowns.mobidic.dictionary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddVocabularyRequestDto {
    @NotBlank
    @Size(min = 1, max = 32, message = "제목은 32자 미만이어야 합니다.")
    private String title;
    @Size(max = 32, message = "설명은 32자 미만이어야 합니다.")
    private String description;
}

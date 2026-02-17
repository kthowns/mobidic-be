package com.kimtaeyang.mobidic.dto;

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
public class AddVocabRequestDto {
    @NotBlank
    @Size(min = 1, max = 32, message = "Invalid title pattern")
    private String title;
    @Size(max = 32, message = "Invalid description pattern")
    private String description;
}

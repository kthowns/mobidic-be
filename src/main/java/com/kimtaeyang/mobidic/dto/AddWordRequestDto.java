package com.kimtaeyang.mobidic.dto;

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
    @Size(min = 1, max = 45, message = "Invalid expression pattern")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z ]*$", message = "Invalid expression pattern")
    private String expression;
}
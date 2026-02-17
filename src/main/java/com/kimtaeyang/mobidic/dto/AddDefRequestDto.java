package com.kimtaeyang.mobidic.dto;

import com.kimtaeyang.mobidic.type.PartOfSpeech;
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
public class AddDefRequestDto {
    @NotBlank
    @Size(max = 32, message = "Invalid definition pattern")
    private String definition;
    @NotNull
    private PartOfSpeech part;
}
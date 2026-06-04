package com.kthowns.mobidic.api.definition.dto.request;

import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateDefinitionRequestDto {
    @NotNull(message = "뜻의 식별자는 필수입니다.")
    private UUID id;
    @NotBlank
    @Size(max = 32, message = "32자 미만이어야 합니다.")
    private String meaning;
    @NotNull(message = "품사는 필수 입력값 입니다.")
    private PartOfSpeech part;
}

package com.kthowns.mobidic.api.word.dto.request;

import com.kthowns.mobidic.api.definition.dto.request.AddDefinitionRequestDto;
import com.kthowns.mobidic.api.definition.dto.request.UpdateDefinitionRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateWordAndDefinitionsRequestDto {
    @NotBlank
    @Size(min = 1, max = 45, message = "단어는 45자 미만이어야 합니다.")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z ]*$", message = "단어는 영문자여야 합니다.")
    private String expression;

    @Valid
    List<UpdateDefinitionRequestDto> updatingDefinitions;

    @Valid
    @Size(max = 10, message = "한 번에 10개 이하의 뜻을 추가할 수 있습니다.")
    List<AddDefinitionRequestDto> addingDefinitions;

    List<UUID> deletingDefinitions;
}
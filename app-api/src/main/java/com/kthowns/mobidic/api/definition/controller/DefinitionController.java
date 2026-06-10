package com.kthowns.mobidic.api.definition.controller;

import com.kthowns.mobidic.api.auth.model.AuthUser;
import com.kthowns.mobidic.api.global.dto.ErrorResponse;
import com.kthowns.mobidic.api.global.dto.GeneralResponse;
import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.service.DefinitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "뜻 관련 서비스", description = "단어 별 뜻 불러오기, 추가, 수정 등")
public class DefinitionController {
    private final DefinitionService definitionService;

    @Operation(
            summary = "뜻 조회",
            description = "단어 식별자를 통한 모든 뜻 조회",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인가되지 않은 요청",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/words/{wordId}/definitions")
    public ResponseEntity<GeneralResponse<List<Definition>>> getDefinitionsByWordId(
            @PathVariable UUID wordId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return GeneralResponse.toResponseEntity(GeneralResponseCode.OK,
                definitionService.getDefinitionsByWordId(authUser.getId(), wordId));
    }
}

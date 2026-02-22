package com.kimtaeyang.mobidic.dictionary.controller;

import com.kimtaeyang.mobidic.common.code.GeneralResponseCode;
import com.kimtaeyang.mobidic.common.dto.ErrorResponse;
import com.kimtaeyang.mobidic.common.dto.GeneralResponse;
import com.kimtaeyang.mobidic.dictionary.dto.AddDefinitionRequestDto;
import com.kimtaeyang.mobidic.dictionary.dto.DefinitionDto;
import com.kimtaeyang.mobidic.dictionary.service.DefinitionService;
import com.kimtaeyang.mobidic.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/definitions")
@Tag(name = "뜻 관련 서비스", description = "단어 별 뜻 불러오기, 추가, 수정 등")
public class DefinitionController {
    private final DefinitionService definitionService;

    @Operation(
            summary = "뜻 추가",
            description = "단어 식별자를 통한 뜻 추가",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인가되지 않은 요청",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "중복된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/{wordId}")
    public ResponseEntity<GeneralResponse<DefinitionDto>> addDefinition(
            @PathVariable String wordId,
            @RequestBody @Valid AddDefinitionRequestDto request,
            @AuthenticationPrincipal User user
    ) {
        return GeneralResponse.toResponseEntity(GeneralResponseCode.OK,
                definitionService.addDefinition(user, UUID.fromString(wordId), request));
    }

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
    @GetMapping("/all")
    public ResponseEntity<GeneralResponse<List<DefinitionDto>>> getDefinitionsByWordId(
            @RequestParam String wordId,
            @AuthenticationPrincipal User user
    ) {
        return GeneralResponse.toResponseEntity(GeneralResponseCode.OK,
                definitionService.getDefinitionsByWordId(user, UUID.fromString(wordId)));
    }

    @Operation(
            summary = "뜻 수정",
            description = "뜻 식별자를 통한 표현과 품사 수정",
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
            @ApiResponse(responseCode = "409", description = "중복된 요청",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PatchMapping("/{definitionId}")
    public ResponseEntity<GeneralResponse<DefinitionDto>> updateDefinition(
            @PathVariable String definitionId,
            @RequestBody @Valid AddDefinitionRequestDto request,
            @AuthenticationPrincipal User user
    ) {
        return GeneralResponse.toResponseEntity(GeneralResponseCode.OK,
                definitionService.updateDefinition(user, UUID.fromString(definitionId), request));
    }

    @Operation(
            summary = "뜻 삭제",
            description = "",
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
    @DeleteMapping("/{definitionId}")
    public ResponseEntity<GeneralResponse<DefinitionDto>> deleteDefinition(
            @PathVariable String definitionId,
            @AuthenticationPrincipal User user
    ) {
        return GeneralResponse.toResponseEntity(GeneralResponseCode.OK,
                definitionService.deleteDefinition(user, UUID.fromString(definitionId)));
    }
}

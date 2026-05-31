package com.kthowns.mobidic.api.vocabulary.controller;

import com.kthowns.mobidic.api.auth.model.AuthUser;
import com.kthowns.mobidic.api.vocabulary.dto.request.AddVocabularyRequestDto;
import com.kthowns.mobidic.common.dto.ErrorResponse;
import com.kthowns.mobidic.common.dto.GeneralResponse;
import com.kthowns.mobidic.domain.vocabulary.model.VocabularyDetail;
import com.kthowns.mobidic.domain.vocabulary.service.VocabularyService;
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

import static com.kthowns.mobidic.common.code.GeneralResponseCode.OK;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/vocabularies")
@Tag(name = "단어장 관련 서비스", description = "사용자별 단어장 목록 불러오기, 추가 등")
public class VocabularyController {
    private final VocabularyService vocabularyService;

    @Operation(
            summary = "단어장 추가",
            description = "중복체크 있음",
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
    @PostMapping
    public ResponseEntity<GeneralResponse<Void>> addVocabulary(
            @RequestBody @Valid AddVocabularyRequestDto request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        vocabularyService.addVocabulary(
                authUser.getId(),
                request.getTitle(),
                request.getDescription()
        );
        return GeneralResponse.toResponseEntity(OK, null);
    }

    @Operation(
            summary = "단어장 조회",
            description = "사용자 식별자를 통한 사용자의 모든 단어장 조회",
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
    @GetMapping
    public ResponseEntity<GeneralResponse<List<VocabularyDetail>>> getAllVocabulary(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return GeneralResponse.toResponseEntity(OK,
                vocabularyService.getVocabularyDetails(authUser.getId()));
    }

    @Operation(
            summary = "단어장 정보 조회",
            description = "단어장 식별자를 통한 단어장 정보 조회",
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
    @GetMapping("/{vocabularyId}")
    public ResponseEntity<GeneralResponse<VocabularyDetail>> getVocabularyDetail(
            @PathVariable UUID vocabularyId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return GeneralResponse.toResponseEntity(OK,
                vocabularyService.getVocabularyById(authUser.getId(), vocabularyId));
    }

    @Operation(
            summary = "단어장 정보 수정",
            description = "단어장 정보 수정, 중복체크 있음",
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
    @PatchMapping("/{vocabularyId}")
    public ResponseEntity<GeneralResponse<Void>> updateVocabulary(
            @PathVariable UUID vocabularyId,
            @RequestBody @Valid AddVocabularyRequestDto request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        vocabularyService.updateVocabulary(
                authUser.getId(),
                vocabularyId,
                request.getTitle(),
                request.getDescription()
        );
        return GeneralResponse.toResponseEntity(OK, null);
    }

    @Operation(
            summary = "단어장 삭제",
            description = "완전 삭제",
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
    @DeleteMapping("/{vocabularyId}")
    public ResponseEntity<GeneralResponse<Void>> deleteVocabulary(
            @PathVariable UUID vocabularyId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        vocabularyService.deleteVocab(authUser.getId(), vocabularyId);
        return GeneralResponse.toResponseEntity(OK, null);
    }
}

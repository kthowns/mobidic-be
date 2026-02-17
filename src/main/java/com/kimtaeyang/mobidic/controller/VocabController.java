package com.kimtaeyang.mobidic.controller;

import com.kimtaeyang.mobidic.dto.AddVocabRequestDto;
import com.kimtaeyang.mobidic.dto.VocabDto;
import com.kimtaeyang.mobidic.dto.response.ErrorResponse;
import com.kimtaeyang.mobidic.dto.response.GeneralResponse;
import com.kimtaeyang.mobidic.service.VocabService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.kimtaeyang.mobidic.code.GeneralResponseCode.OK;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/vocab")
@Tag(name = "단어장 관련 서비스", description = "사용자별 단어장 목록 불러오기, 추가 등")
public class VocabController {
    private final VocabService vocabService;

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
    @PostMapping("/{memberId}")
    public ResponseEntity<GeneralResponse<VocabDto>> addVocab(
            @PathVariable String memberId,
            @RequestBody @Valid AddVocabRequestDto request
    ) {
        return GeneralResponse.toResponseEntity(OK,
                vocabService.addVocab(UUID.fromString(memberId), request));
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
    @GetMapping("/all")
    public ResponseEntity<GeneralResponse<List<VocabDto>>> getAllVocab(
            @RequestParam String uId
    ) {
        return GeneralResponse.toResponseEntity(OK,
                vocabService.getVocabsByMemberId(UUID.fromString(uId)));
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
    @GetMapping("/detail")
    public ResponseEntity<GeneralResponse<VocabDto>> getVocabDetail(
            @RequestParam String vId
    ) {
        return GeneralResponse.toResponseEntity(OK,
                vocabService.getVocabById(UUID.fromString(vId)));
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
    @PatchMapping("/{vocabId}")
    public ResponseEntity<GeneralResponse<VocabDto>> updateVocab(
            @PathVariable String vocabId,
            @RequestBody @Valid AddVocabRequestDto request
    ) {
        return GeneralResponse.toResponseEntity(OK,
                vocabService.updateVocab(UUID.fromString(vocabId), request));
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
    @DeleteMapping("/{vocabId}")
    public ResponseEntity<GeneralResponse<VocabDto>> deleteVocab(
            @PathVariable String vocabId
    ) {
        return GeneralResponse.toResponseEntity(OK,
                vocabService.deleteVocab(UUID.fromString(vocabId)));
    }
}

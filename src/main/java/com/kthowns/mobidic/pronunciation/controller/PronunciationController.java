package com.kthowns.mobidic.pronunciation.controller;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.dto.ErrorResponse;
import com.kthowns.mobidic.common.dto.GeneralResponse;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.pronunciation.service.PronunciationService;
import com.kthowns.mobidic.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static com.kthowns.mobidic.common.code.GeneralResponseCode.OK;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
@Tag(name = "발음 체크 관련 서비스", description = "발음 점수 체크 등")
public class PronunciationController {
    private final PronunciationService pronunciationService;

    @Operation(
            summary = "발음 체크",
            description = "음성 파일과 단어 식별자를 통한 발음 점수 체크, 0~1 사이의 실수, 파일 크기는 100KB 이내",
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
            @ApiResponse(responseCode = "413", description = "너무 큰 파일 용량",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping(value = "/words/{wordId}/pronunciation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeneralResponse<Double>> ratePronunciation(
            @RequestParam MultipartFile file,
            @PathVariable UUID wordId,
            @AuthenticationPrincipal User user
    ) {
        if (file.getSize() > 500 * 1024) { // Allow file size under 500KB
            throw new ApiException(GeneralResponseCode.TOO_BIG_FILE_SIZE);
        }

        return GeneralResponse.toResponseEntity(OK,
                pronunciationService.ratePronunciation(user, wordId, file));
    }
}

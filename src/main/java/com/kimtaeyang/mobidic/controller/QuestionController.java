package com.kimtaeyang.mobidic.controller;

import com.kimtaeyang.mobidic.dto.QuestionDto;
import com.kimtaeyang.mobidic.dto.QuestionRateDto;
import com.kimtaeyang.mobidic.dto.response.ErrorResponse;
import com.kimtaeyang.mobidic.dto.response.GeneralResponse;
import com.kimtaeyang.mobidic.service.CryptoService;
import com.kimtaeyang.mobidic.service.QuestionService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.kimtaeyang.mobidic.code.GeneralResponseCode.OK;

@Tag(name = "퀴즈 관련 서비스", description = "문제 생성 및 채점 등")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/quiz")
public class QuestionController {
    private final QuestionService questionService;
    private final CryptoService cryptoService;

    @Operation(
            summary = "OX 퀴즈 생성",
            description = "단어장 식별자를 통해 단어장에 속한 단어들로 문제 생성",
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
    @GetMapping("/generate/ox")
    public ResponseEntity<GeneralResponse<List<QuestionDto>>> getOxQuiz(
            @RequestParam("vId") UUID vId
    ) {
        return GeneralResponse.toResponseEntity(OK,
                questionService.getOxQuestions(vId));
    }

    @Operation(
            summary = "빈칸 채우기 생성",
            description = "단어장 식별자를 통해 단어장에 속한 단어들로 문제 생성",
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
    @GetMapping("/generate/blank")
    public ResponseEntity<GeneralResponse<List<QuestionDto>>> getBlankQuiz(
            @RequestParam("vId") UUID vId
    ) {
        return GeneralResponse.toResponseEntity(OK,
                questionService.getBlankQuestions(vId));
    }

    @Operation(
            summary = "퀴즈 채점",
            description = "퀴즈 생성 시 반환된 문제별 토큰과 사용자 입력 값을 통해 채점",
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
            @ApiResponse(responseCode = "408", description = "문제 풀이 1분 타임 아웃",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/rate")
    public ResponseEntity<GeneralResponse<QuestionRateDto.Response>> rateOxQuiz(
            @RequestBody QuestionRateDto.Request request
    ) {
        UUID memberId = UUID.fromString(cryptoService.decrypt(request.getToken()).split(":")[1]);

        return GeneralResponse.toResponseEntity(OK,
                questionService.rateQuestion(memberId, request));
    }
}

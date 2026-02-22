package com.kimtaeyang.mobidic.quiz.controller;

import com.kimtaeyang.mobidic.common.dto.ErrorResponse;
import com.kimtaeyang.mobidic.common.dto.GeneralResponse;
import com.kimtaeyang.mobidic.quiz.dto.QuizDto;
import com.kimtaeyang.mobidic.quiz.dto.QuizStatisticDto;
import com.kimtaeyang.mobidic.quiz.service.CryptoService;
import com.kimtaeyang.mobidic.quiz.service.QuizService;
import com.kimtaeyang.mobidic.user.entity.User;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.kimtaeyang.mobidic.common.code.GeneralResponseCode.OK;

@Tag(name = "퀴즈 관련 서비스", description = "문제 생성 및 채점 등")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/quizs")
public class QuizController {
    private final QuizService quizService;
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
    @GetMapping("/ox")
    public ResponseEntity<GeneralResponse<List<QuizDto>>> getOxQuizzes(
            @RequestParam UUID vocabularyId,
            @AuthenticationPrincipal User user
    ) {
        return GeneralResponse.toResponseEntity(OK,
                quizService.getOXQuizzes(user, vocabularyId));
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
    @GetMapping("/blank")
    public ResponseEntity<GeneralResponse<List<QuizDto>>> getBlankQuizzes(
            @RequestParam UUID vocabularyId,
            @AuthenticationPrincipal User user
    ) {
        return GeneralResponse.toResponseEntity(OK,
                quizService.getBlankQuizzes(user, vocabularyId));
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
    public ResponseEntity<GeneralResponse<QuizStatisticDto.Response>> rateOxQuiz(
            @RequestBody QuizStatisticDto.Request request,
            @AuthenticationPrincipal User user
    ) {
        return GeneralResponse.toResponseEntity(OK,
                quizService.rateQuestion(user, request));
    }
}

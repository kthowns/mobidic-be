package com.kthowns.mobidic.api.statistic.controller;

import com.kthowns.mobidic.api.auth.model.AuthUser;
import com.kthowns.mobidic.api.global.dto.ErrorResponse;
import com.kthowns.mobidic.api.global.dto.GeneralResponse;
import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.domain.statistic.service.StatisticService;
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

import java.util.UUID;

import static com.kthowns.mobidic.common.code.GeneralResponseCode.OK;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
@Tag(name = "통계 관련 서비스", description = "단어장 별 학습률, 단어 난이도 불러오기 등")
public class StatisticController {
    private final StatisticService statisticService;

    @Operation(
            summary = "단어 통계 조회",
            description = "단어 식별자를 통한 단어 통계 조회, 틀린 횟수 맞은 횟수 등",
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
    @GetMapping("/words/{wordId}/statistic")
    public ResponseEntity<GeneralResponse<WordStatistic>> getWordStatisticById(
            @PathVariable UUID wordId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return GeneralResponse.toResponseEntity(OK,
                statisticService.getWordStatisticById(authUser.getId(), wordId));
    }

    @Operation(
            summary = "단어장 학습률 조회",
            description = "단어장 내의 학습된 단어 비율 0~1 사이의 실수로 반환",
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
    @GetMapping("/vocabularies/{vocabularyId}/learning-rate")
    public ResponseEntity<GeneralResponse<Double>> getVocabLearningRate(
            @PathVariable UUID vocabularyId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return GeneralResponse.toResponseEntity(OK,
                statisticService.getVocabLearningRate(authUser.getId(), vocabularyId));
    }

    @Operation(
            summary = "단어장 퀴즈 정답률 조회",
            description = "단어의 학습 여부 토글링",
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
    @GetMapping("/vocabularies/{vocabularyId}/accuracy")
    public ResponseEntity<GeneralResponse<Double>> getAvgAccuracyByVocab(
            @PathVariable UUID vocabularyId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return GeneralResponse.toResponseEntity(OK,
                statisticService.getAvgAccuracyByVocab(authUser.getId(), vocabularyId));
    }

    @Operation(
            summary = "모든 단어장 퀴즈 정답률 조회",
            description = "사용자 식별자를 통한 모든 단어장의 퀴즈 정답률 평균 조회",
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
    @GetMapping("/users/me/accuracy")
    public ResponseEntity<GeneralResponse<Double>> getAvgAccuracyOfAll(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return GeneralResponse.toResponseEntity(OK,
                statisticService.getTotalAvgAccuracy(authUser.getId()));
    }

    @Operation(
            summary = "단어 학습 여부 토글",
            description = "단어의 학습 여부 토글링",
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
    @PatchMapping("/words/{wordId}/toggle-learned")
    public ResponseEntity<GeneralResponse<Void>> toggleLearnedByWordId(
            @PathVariable UUID wordId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        statisticService.toggleLearnedByWordId(authUser.getId(), wordId);

        return GeneralResponse.toResponseEntity(OK, null);
    }
}

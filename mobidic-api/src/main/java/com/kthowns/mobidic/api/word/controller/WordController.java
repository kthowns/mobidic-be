package com.kthowns.mobidic.api.word.controller;

import com.kthowns.mobidic.api.auth.model.AuthUser;
import com.kthowns.mobidic.api.definition.util.DefinitionCommandMapper;
import com.kthowns.mobidic.api.word.dto.request.AddWordRequestDto;
import com.kthowns.mobidic.api.word.dto.request.UpdateWordAndDefinitionsRequestDto;
import com.kthowns.mobidic.api.word.util.WordCommandMapper;
import com.kthowns.mobidic.common.dto.ErrorResponse;
import com.kthowns.mobidic.common.dto.GeneralResponse;
import com.kthowns.mobidic.domain.word.facade.WordFacade;
import com.kthowns.mobidic.domain.word.model.WordDetail;
import com.kthowns.mobidic.domain.word.service.WordService;
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
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "단어 관련 서비스", description = "단어장 별 단어 불러오기, 추가 등")
public class WordController {
    private final WordService wordService;
    private final WordFacade wordFacade;
    private final WordCommandMapper wordCommandMapper;
    private final DefinitionCommandMapper definitionCommandMapper;

    @Operation(
            summary = "단어 전체 조회",
            description = "단어장 식별자를 통한 단어 전체 조회",
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
    @GetMapping("/vocabularies/{vocabularyId}/words")
    public ResponseEntity<GeneralResponse<List<WordDetail>>> getWordsByVocabularyId(
            @PathVariable UUID vocabularyId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return GeneralResponse.toResponseEntity(OK,
                wordService.getWordDetailsByVocabularyId(authUser.getId(), vocabularyId));
    }

    @Operation(
            summary = "단어 추가",
            description = "중복체크 있음, 최대 45자",
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
    @PostMapping("/vocabularies/{vocabularyId}/word")
    public ResponseEntity<GeneralResponse<Void>> addWord(
            @PathVariable UUID vocabularyId,
            @RequestBody @Valid AddWordRequestDto request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        wordFacade.addWord(
                authUser.getId(),
                vocabularyId,
                wordCommandMapper.toAddWordCommand(request),
                definitionCommandMapper.toAddDefinitionCommands(request.getDefinitions())
        );
        return GeneralResponse.toResponseEntity(OK, null);
    }

    @Operation(
            summary = "단어 수정",
            description = "최대 45자",
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
    @PatchMapping("/words/{wordId}")
    public ResponseEntity<GeneralResponse<Void>> updateWord(
            @PathVariable UUID wordId,
            @RequestBody @Valid UpdateWordAndDefinitionsRequestDto request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        wordFacade.updateWordAndSyncDefinitions(
                authUser.getId(),
                wordCommandMapper.toUpdateWordCommand(request, wordId),
                definitionCommandMapper.toUpdateDefinitionCommands(request.getUpdatingDefinitions(), wordId),
                definitionCommandMapper.toAddDefinitionCommands(request.getAddingDefinitions()),
                request.getDeletingDefinitions()
        );
        return GeneralResponse.toResponseEntity(OK, null);
    }

    @Operation(
            summary = "단어 삭제",
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
    @DeleteMapping("/words/{wordId}")
    public ResponseEntity<GeneralResponse<Void>> deleteWord(
            @PathVariable UUID wordId,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        wordService.deleteWord(authUser.getId(), wordId);
        return GeneralResponse.toResponseEntity(OK, null);
    }
}

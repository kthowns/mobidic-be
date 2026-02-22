package com.kimtaeyang.mobidic.user.controller;

import com.kimtaeyang.mobidic.common.dto.ErrorResponse;
import com.kimtaeyang.mobidic.common.dto.GeneralResponse;
import com.kimtaeyang.mobidic.user.dto.UpdateNicknameRequestDto;
import com.kimtaeyang.mobidic.user.dto.UpdatePasswordRequestDto;
import com.kimtaeyang.mobidic.user.dto.UserDto;
import com.kimtaeyang.mobidic.user.entity.User;
import com.kimtaeyang.mobidic.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.kimtaeyang.mobidic.common.code.GeneralResponseCode.OK;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/user")
@Tag(name = "사용자 관련 서비스", description = "닉네임 및 패스워드 변경, 회원탈퇴 등")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "사용자 정보 조회",
            description = "식별자를 통한 사용자 정보 조희",
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
    public ResponseEntity<GeneralResponse<UserDto>> getUserDetail(
            @RequestParam @Valid String uId
    ) {
        return GeneralResponse.toResponseEntity(OK,
                userService.getUserDetailById(UUID.fromString(uId)));
    }

    @Operation(
            summary = "닉네임 변경",
            description = "닉네임 변경, 중복체크 있음",
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
    @PatchMapping("/{userId}/nickname")
    public ResponseEntity<GeneralResponse<UserDto>> updateUserNickname(
            @PathVariable String userId,
            @RequestBody @Valid UpdateNicknameRequestDto request
    ) {
        return GeneralResponse.toResponseEntity(OK,
                userService.updateUserNickname(UUID.fromString(userId), request));
    }

    @Operation(
            summary = "비밀번호 변경",
            description = "비밀번호 변경, 8자 이상/알파벳+숫자",
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
    @PatchMapping("/{userId}/password")
    public ResponseEntity<GeneralResponse<UserDto>> updateUserPassword(
            @PathVariable String userId,
            @RequestBody @Valid UpdatePasswordRequestDto request,
            HttpServletRequest httpServletRequest
    ) {
        String token = httpServletRequest.getHeader("Authorization").substring(7);

        return GeneralResponse.toResponseEntity(OK,
                userService.updateUserPassword(UUID.fromString(userId), request, token));
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "완전 삭제는 아니며 계정 비활성화",
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
    @DeleteMapping("/")
    public ResponseEntity<GeneralResponse<UserDto>> deactivateUser(
            @AuthenticationPrincipal User user
    ) {
        return GeneralResponse.toResponseEntity(OK,
                userService.deactivateUser(user));
    }
}

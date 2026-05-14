package com.kthowns.mobidic.api.controller.user;

import com.kthowns.mobidic.api.dto.request.user.SignUpRequestDto;
import com.kthowns.mobidic.common.dto.ErrorResponse;
import com.kthowns.mobidic.common.dto.GeneralResponse;
import com.kthowns.mobidic.api.dto.request.user.UpdateUserRequestDto;
import com.kthowns.mobidic.api.dto.common.user.UserDto;
import com.kthowns.mobidic.api.user.entity.User;
import com.kthowns.mobidic.api.user.facade.UserFacade;
import com.kthowns.mobidic.api.user.service.UserService;
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

import static com.kthowns.mobidic.common.code.GeneralResponseCode.OK;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users")
@Tag(name = "사용자 관련 서비스", description = "닉네임 및 패스워드 변경, 회원탈퇴 등")
public class UserController {
    private final UserService userService;
    private final UserFacade userFacade;

    @Operation(
            summary = "현재사용자 정보 조회",
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
    @GetMapping("/me")
    public ResponseEntity<GeneralResponse<UserDto>> getMe(
            @AuthenticationPrincipal User user
    ) {
        return GeneralResponse.toResponseEntity(OK, UserDto.fromEntity(user));
    }

    @Operation(
            summary = "사용자 정보 변경",
            description = "정보 변경, 닉네임 중복체크 있음",
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
    @PatchMapping("/me")
    public ResponseEntity<GeneralResponse<UserDto>> updateMe(
            @RequestBody @Valid UpdateUserRequestDto request,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpServletRequest
    ) {
        String token = httpServletRequest.getHeader("Authorization").substring(7);

        return GeneralResponse.toResponseEntity(OK,
                userService.updateUser(user, request, token));
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
    @DeleteMapping("/me")
    public ResponseEntity<GeneralResponse<UserDto>> deactivateUser(
            @AuthenticationPrincipal User user,
            HttpServletRequest httpServletRequest
    ) {
        String token = httpServletRequest.getHeader("Authorization").substring(7);

        return GeneralResponse.toResponseEntity(OK,
                userService.deactivateUser(user, token));
    }

    @Operation(
            summary = "회원가입",
            description = "회원가입"
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
    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequestDto request) {
        userFacade.signUp(request);

        return ResponseEntity.ok().build();
    }
}

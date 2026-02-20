package com.kimtaeyang.mobidic.auth.controller;

import com.kimtaeyang.mobidic.auth.dto.LoginRequest;
import com.kimtaeyang.mobidic.auth.dto.LoginResponse;
import com.kimtaeyang.mobidic.auth.dto.SignUpRequestDto;
import com.kimtaeyang.mobidic.auth.service.AuthService;
import com.kimtaeyang.mobidic.common.code.AuthResponseCode;
import com.kimtaeyang.mobidic.common.dto.ErrorResponse;
import com.kimtaeyang.mobidic.common.dto.GeneralResponse;
import com.kimtaeyang.mobidic.user.dto.UserDto;
import com.kimtaeyang.mobidic.user.entity.User;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "인증 관련 서비스", description = "로그인, 회원가입 등")
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인가되지 않은 요청",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/login")
    public ResponseEntity<GeneralResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return GeneralResponse.toResponseEntity(AuthResponseCode.LOGIN_OK, authService.login(request));
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
    public ResponseEntity<GeneralResponse<UserDto>> signUp(@Valid @RequestBody SignUpRequestDto request) {
        return GeneralResponse.toResponseEntity(AuthResponseCode.SIGN_UP_OK, authService.signUp(request));
    }

    @Operation(
            summary = "로그아웃",
            description = "토큰 정보 만으로 로그아웃",
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
    @PostMapping("/logout")
    public ResponseEntity<GeneralResponse<UserDto>> logout(
            HttpServletRequest request,
            @AuthenticationPrincipal User user
    ) {
        String token = request.getHeader("Authorization").substring(7);
        UUID userId = user.getId();

        return GeneralResponse.toResponseEntity(AuthResponseCode.LOGOUT_OK, authService.logout(userId, token));
    }
}
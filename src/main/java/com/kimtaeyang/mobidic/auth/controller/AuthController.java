package com.kimtaeyang.mobidic.auth.controller;

import com.kimtaeyang.mobidic.auth.dto.KakaoLoginUrlResponse;
import com.kimtaeyang.mobidic.auth.dto.LoginRequest;
import com.kimtaeyang.mobidic.auth.dto.LoginResponse;
import com.kimtaeyang.mobidic.auth.service.AuthService;
import com.kimtaeyang.mobidic.auth.service.KakaoAuthService;
import com.kimtaeyang.mobidic.common.code.AuthResponseCode;
import com.kimtaeyang.mobidic.common.code.GeneralResponseCode;
import com.kimtaeyang.mobidic.common.dto.ErrorResponse;
import com.kimtaeyang.mobidic.common.dto.GeneralResponse;
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
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "인증 관련 서비스", description = "로그인, 회원가입 등")
public class AuthController {
    private final AuthService authService;
    private final KakaoAuthService kakaoAuthService;

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
    public ResponseEntity<Void> logout(
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization").substring(7);

        authService.logout(token);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/login-url/kakao")
    public ResponseEntity<GeneralResponse<KakaoLoginUrlResponse>> getKakaoLoginUrl() {
        return GeneralResponse.toResponseEntity(GeneralResponseCode.OK, kakaoAuthService.getKakaoLoginUrl(false));
    }

    @GetMapping("/v1/oauth2/kakao")
    public ResponseEntity<GeneralResponse<LoginResponse>> kakaoLogin(
            @RequestParam String code
    ) {
        return GeneralResponse.toResponseEntity(GeneralResponseCode.OK, kakaoAuthService.kakaoLogin(code, false));
    }

    @Profile("dev")
    @GetMapping("/dev/login-url/kakao")
    public ResponseEntity<GeneralResponse<KakaoLoginUrlResponse>> getDevKakaoLoginUrl() {
        return GeneralResponse.toResponseEntity(GeneralResponseCode.OK, kakaoAuthService.getKakaoLoginUrl(true));
    }


    @Profile("dev")
    @GetMapping("/dev/v1/oauth2/kakao")
    public ResponseEntity<GeneralResponse<LoginResponse>> kakaoDevLogin(
            @RequestParam String code
    ) {
        return GeneralResponse.toResponseEntity(GeneralResponseCode.OK, kakaoAuthService.kakaoLogin(code, true));
    }
}
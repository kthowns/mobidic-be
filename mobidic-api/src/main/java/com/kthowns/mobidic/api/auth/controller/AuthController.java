package com.kthowns.mobidic.api.auth.controller;

import com.kthowns.mobidic.api.auth.dto.request.LoginRequest;
import com.kthowns.mobidic.api.auth.dto.response.LoginResponse;
import com.kthowns.mobidic.api.auth.model.AuthUser;
import com.kthowns.mobidic.api.security.jwt.JwtProvider;
import com.kthowns.mobidic.common.code.AuthResponseCode;
import com.kthowns.mobidic.common.dto.ErrorResponse;
import com.kthowns.mobidic.common.dto.GeneralResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "인증 관련 서비스", description = "로그인, 회원가입 등")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

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
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        AuthUser authUser = (AuthUser) auth.getPrincipal();

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(jwtProvider.generateToken(authUser.getId(), authUser.getRole()))
                .build();

        return GeneralResponse.toResponseEntity(AuthResponseCode.LOGIN_OK, loginResponse);
    }
}
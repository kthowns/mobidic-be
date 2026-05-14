package com.kthowns.mobidic.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum AuthResponseCode implements ApiResponseCode {
    NO_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰 입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "인가되지 않은 요청 입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 요청 입니다."),
    
    LOGIN_OK(HttpStatus.OK, "로그인에 성공했습니다.");

    private final HttpStatus status;
    private final String message;
}

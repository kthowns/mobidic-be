package com.kimtaeyang.mobidic.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum GeneralResponseCode implements ApiResponseCode {
    OK(HttpStatus.OK, "OK"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Not found"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Invalid Request"),

    NO_VOCAB(HttpStatus.NOT_FOUND, "단어장을 찾을 수 없습니다."),
    NO_WORD(HttpStatus.NOT_FOUND, "단어를 찾을 수 없습니다."),
    NO_DEF(HttpStatus.NOT_FOUND, "뜻을 찾을 수 없습니다."),
    NO_STAT(HttpStatus.NOT_FOUND, "통계 정보를 찾을 수 없습니다."),
    NO_QUIZ(HttpStatus.NOT_FOUND, "해당 퀴즈를 찾을 수 없습니다."),

    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "중복되는 이메일 입니다."),
    DUPLICATED_NICKNAME(HttpStatus.CONFLICT, "중복되는 닉네임 입니다."),
    DUPLICATED_TITLE(HttpStatus.CONFLICT, "중복되는 제목 입니다."),
    DUPLICATED_WORD(HttpStatus.CONFLICT, "중복되는 단어 입니다."),
    DUPLICATED_DEFINITION(HttpStatus.CONFLICT, "중복되는 뜻 입니다."),

    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, "유효하지 않은 요청입니다."),
    REQUEST_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "요청 시간이 초과 되었습니다."),
    TOO_BIG_FILE_SIZE(HttpStatus.BAD_REQUEST, "파일 크기가 너무 큽니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부에서 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}

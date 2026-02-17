package com.kimtaeyang.mobidic.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum GeneralResponseCode implements ApiResponseCode {
    OK(HttpStatus.OK, "OK"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Not found"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Invalid Request"),
    NO_VOCAB(HttpStatus.NOT_FOUND, "No vocab found"),
    NO_WORD(HttpStatus.NOT_FOUND, "No word found"),
    NO_DEF(HttpStatus.NOT_FOUND, "No def found"),
    NO_THEME(HttpStatus.NOT_FOUND, "No theme found"),
    NO_RATE(HttpStatus.NOT_FOUND, "No rate found"),
    NO_QUIZ(HttpStatus.NOT_FOUND, "No quiz found"),
    EMPTY_VOCAB(HttpStatus.NOT_FOUND, "No vocab found"),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "Email is duplicated"),
    DUPLICATED_NICKNAME(HttpStatus.CONFLICT, "Nickname is duplicated"),
    DUPLICATED_TITLE(HttpStatus.CONFLICT, "Title is duplicated"),
    DUPLICATED_WORD(HttpStatus.CONFLICT, "Word is duplicated"),
    DUPLICATED_DEFINITION(HttpStatus.CONFLICT, "Definition is duplicated"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request"),
    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, "Invalid request body"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Forbidden request"),
    REQUEST_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "Request timeout"),
    TOO_BIG_FILE_SIZE(HttpStatus.BAD_REQUEST, "Too big file size"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    private final HttpStatus status;
    private final String message;
}

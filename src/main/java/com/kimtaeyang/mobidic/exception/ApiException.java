package com.kimtaeyang.mobidic.exception;

import com.kimtaeyang.mobidic.code.ApiResponseCode;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
    private final ApiResponseCode responseCode;
    private final HttpStatus status;
    private final String message;

    public ApiException(ApiResponseCode responseCode){
        super(responseCode.getMessage());
        this.responseCode = responseCode;
        this.status = responseCode.getStatus();
        this.message = responseCode.getMessage();
    }

    public ApiException(ApiResponseCode responseCode, String message){
        super(responseCode.getMessage());
        this.responseCode = responseCode;
        this.status = responseCode.getStatus();
        this.message = message;
    }
}

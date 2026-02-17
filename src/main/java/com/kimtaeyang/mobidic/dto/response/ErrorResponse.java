package com.kimtaeyang.mobidic.dto.response;

import com.kimtaeyang.mobidic.code.ApiResponseCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
    private Integer status;
    private String message;
    private HashMap<String, String> errors;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ApiResponseCode responseCode, HashMap<String, String> errors) {
        return ResponseEntity.status(responseCode.getStatus())
                .body(ErrorResponse.fromData(
                        responseCode, errors));
    }

    private static ErrorResponse fromData(ApiResponseCode responseCode, HashMap<String, String> errors) {
        return ErrorResponse.builder()
                .errors(errors)
                .message(responseCode.getMessage())
                .status(responseCode.getStatus().value())
                .build();
    }
}
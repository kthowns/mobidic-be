package com.kimtaeyang.mobidic.dto.response;

import com.kimtaeyang.mobidic.code.ApiResponseCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeneralResponse<T> {
    private Integer status;
    private String message;
    private T data;

    public static <T> ResponseEntity<GeneralResponse<T>> toResponseEntity(ApiResponseCode responseCode, T data){
        return ResponseEntity.status(responseCode.getStatus())
                .body(GeneralResponse.fromData(
                        responseCode, data));
    }

    private static <T> GeneralResponse<T> fromData(ApiResponseCode responseCode, T data){
        return GeneralResponse.<T>builder()
                .data(data)
                .message(responseCode.getMessage())
                .status(responseCode.getStatus().value())
                .build();
    }
}

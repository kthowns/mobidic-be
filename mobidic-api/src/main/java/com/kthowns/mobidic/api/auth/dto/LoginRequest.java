package com.kthowns.mobidic.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank
    @Email(message = "유효하지 않은 이메일 형식입니다.")
    private String email;
    @NotBlank
    private String password;
}

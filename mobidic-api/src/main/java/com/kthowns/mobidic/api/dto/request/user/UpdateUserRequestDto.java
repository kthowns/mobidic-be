package com.kthowns.mobidic.api.dto.request.user;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequestDto {
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,16}$", message = "유효하지 않은 닉네임 형식 입니다.")
    private String nickname;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,128}$", message = "비밀번호는 8자 이상, 영문자와 숫자를 포함해야 합니다.")
    //최소 8자, 숫자와 알파벳
    private String password;
}
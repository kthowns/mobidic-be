package com.kthowns.mobidic.api.user.dto.request;

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
    @Pattern(
            regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,16}$",
            message = "닉네임은 2~16자의 한글, 영문 소문자, 숫자, -, _ 만 사용할 수 있습니다."
    )
    private String nickname;

    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,128}$",
            message = "비밀번호는 8~128자이며 영문자, 숫자, 특수문자(@$!%*?&)를 각각 1개 이상 포함해야 합니다."
    )
    private String password;
}

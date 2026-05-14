package com.kthowns.mobidic.api.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SignUpRequestDto {
    @NotBlank(message = "이메일을 입력해주세요")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,100}$",
            message = "유효하지 않은 이메일 형식입니다."
    )
    private String email;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Pattern(
            regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,16}$",
            message = "닉네임은 2~16자의 한글, 영문 소문자, 숫자, -, _ 만 사용할 수 있습니다."
    )
    private String nickname;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,128}$",
            message = "비밀번호는 8~128자이며 영문자, 숫자, 특수문자(@$!%*?&)를 각각 1개 이상 포함해야 합니다."
    )
    //최소 8자, 숫자와 알파벳
    private String password;

    @NotNull(message = "이용 약관 동의 항목은 필수입니다.")
    private List<Long> agreeTermIds;
}

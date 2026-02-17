package com.kimtaeyang.mobidic.controller;

import com.kimtaeyang.mobidic.dto.member.MemberDto;
import com.kimtaeyang.mobidic.dto.member.UpdateNicknameRequestDto;
import com.kimtaeyang.mobidic.dto.member.UpdatePasswordRequestDto;
import com.kimtaeyang.mobidic.dto.response.ErrorResponse;
import com.kimtaeyang.mobidic.dto.response.GeneralResponse;
import com.kimtaeyang.mobidic.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.kimtaeyang.mobidic.code.GeneralResponseCode.OK;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/user")
@Tag(name = "사용자 관련 서비스", description = "닉네임 및 패스워드 변경, 회원탈퇴 등")
public class MemberController {
    private final MemberService memberService;

    @Operation(
            summary = "사용자 정보 조회",
            description = "식별자를 통한 사용자 정보 조희",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인가되지 않은 요청",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/detail")
    public ResponseEntity<GeneralResponse<MemberDto>> getMemberDetail(
            @RequestParam @Valid String uId
    ) {
        return GeneralResponse.toResponseEntity(OK,
                memberService.getMemberDetailById(UUID.fromString(uId)));
    }

    @Operation(
            summary = "닉네임 변경",
            description = "닉네임 변경, 중복체크 있음",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인가되지 않은 요청",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "중복된 요청",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PatchMapping("/nckchn/{memberId}")
    public ResponseEntity<GeneralResponse<MemberDto>> updateMemberNickname(
            @PathVariable String memberId,
            @RequestBody @Valid UpdateNicknameRequestDto request
    ) {
        return GeneralResponse.toResponseEntity(OK,
                memberService.updateMemberNickname(UUID.fromString(memberId), request));
    }

    @Operation(
            summary = "비밀번호 변경",
            description = "비밀번호 변경, 8자 이상/알파벳+숫자",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인가되지 않은 요청",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PatchMapping("/pschn/{memberId}")
    public ResponseEntity<GeneralResponse<MemberDto>> updateMemberPassword(
            @PathVariable String memberId,
            @RequestBody @Valid UpdatePasswordRequestDto request,
            HttpServletRequest httpServletRequest
    ) {
        String token = httpServletRequest.getHeader("Authorization").substring(7);

        return GeneralResponse.toResponseEntity(OK,
                memberService.updateMemberPassword(UUID.fromString(memberId), request, token));
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "완전 삭제는 아니며 계정 비활성화",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인가되지 않은 요청",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PatchMapping("/withdraw/{memberId}")
    public ResponseEntity<GeneralResponse<MemberDto>> withdrawMember(
            @PathVariable String memberId,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization").substring(7);

        return GeneralResponse.toResponseEntity(OK,
                memberService.withdrawMember(token, UUID.fromString(memberId)));
    }

    @Operation(
            summary = "회원 삭제",
            description = "테스트 용으로 실 사용 X",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인가되지 않은 요청",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping("/delete/{memberId}")
    public ResponseEntity<GeneralResponse<MemberDto>> deleteMember(
            @PathVariable String memberId,
            HttpServletRequest servletRequest
    ) {
        String token = servletRequest.getHeader("Authorization").substring(7);

        return GeneralResponse.toResponseEntity(OK,
                memberService.deleteMember(token, UUID.fromString(memberId)));
    }
}

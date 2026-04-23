package com.kimtaeyang.mobidic.term.controller;

import com.kimtaeyang.mobidic.common.dto.GeneralResponse;
import com.kimtaeyang.mobidic.term.dto.AddTermRequest;
import com.kimtaeyang.mobidic.term.dto.TermSimpleDto;
import com.kimtaeyang.mobidic.term.service.TermService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.kimtaeyang.mobidic.common.code.GeneralResponseCode.OK;

@RestController
@RequiredArgsConstructor
public class TermController {
    private final TermService termService;

    @GetMapping("/api/terms")
    public ResponseEntity<GeneralResponse<List<TermSimpleDto>>> getActiveTerms() {
        List<TermSimpleDto> terms = termService.getActiveTerms();

        return GeneralResponse.toResponseEntity(OK, terms);
    }

    @Operation(
            summary = "이용 약관 추가",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/api/terms")
    public ResponseEntity<GeneralResponse<Void>> addTerm(
            @RequestBody @Valid AddTermRequest addTermRequest
    ) {
        termService.addTerm(addTermRequest);
        return GeneralResponse.toResponseEntity(OK, null);
    }
}

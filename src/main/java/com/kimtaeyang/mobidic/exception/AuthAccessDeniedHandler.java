package com.kimtaeyang.mobidic.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kimtaeyang.mobidic.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.kimtaeyang.mobidic.code.GeneralResponseCode.FORBIDDEN;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuthAccessDeniedHandler implements AccessDeniedHandler {
    //403
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.error("errorCode : {}, uri : {}, message : {}",
                accessDeniedException, request.getRequestURI(), accessDeniedException   .getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(null)
                .status(FORBIDDEN.getStatus().value())
                .message(FORBIDDEN.getMessage())
                .build();

        String responseBody = objectMapper.writeValueAsString(errorResponse);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(errorResponse.getStatus());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseBody);
    }
}
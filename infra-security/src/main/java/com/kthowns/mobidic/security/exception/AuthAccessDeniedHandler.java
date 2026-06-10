package com.kthowns.mobidic.security.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@Slf4j
public class AuthAccessDeniedHandler implements AccessDeniedHandler {
    //403
    private final HandlerExceptionResolver handlerExceptionResolver;

    public AuthAccessDeniedHandler(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) {
        log.error("errorCode : {}, uri : {}, message : {}",
                accessDeniedException, request.getRequestURI(), accessDeniedException.getMessage());

        handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);
    }
}
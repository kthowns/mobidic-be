package com.kthowns.mobidic.security.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@Slf4j
public class AuthAuthenticationEntryPoint implements AuthenticationEntryPoint {
    //401
    private final HandlerExceptionResolver handlerExceptionResolver;

    public AuthAuthenticationEntryPoint(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) {
        log.error("errorCode : {}, uri : {}, message : {}",
                authException, request.getRequestURI(), authException.getMessage());

        handlerExceptionResolver.resolveException(request, response, null, authException);
    }
}
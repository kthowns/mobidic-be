package com.kthowns.mobidic.api.term.exception;

import com.kthowns.mobidic.api.common.code.GeneralResponseCode;
import com.kthowns.mobidic.api.common.exception.ApiException;
import com.kthowns.mobidic.api.term.controller.TermViewController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice(assignableTypes = TermViewController.class)
public class TermViewExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public String apiExceptionHandler(
            ApiException e,
            Model model
    ) {
        log.error(e.getMessage(), e);
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String illegalArgumentExceptionHandler(
            IllegalArgumentException e,
            Model model
    ) {
        log.error(e.getMessage());
        model.addAttribute("errorMessage", GeneralResponseCode.NO_TERM.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String exceptionHandler(
            Exception e,
            Model model
    ) {
        log.error(e.getMessage(), e);
        model.addAttribute("errorMessage", "알 수 없는 오류가 발생했습니다.");
        return "error";
    }
}

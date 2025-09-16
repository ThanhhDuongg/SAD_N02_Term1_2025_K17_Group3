package com.example.dorm.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public String handleBadRequest(RuntimeException ex, Model model) {
        log.error("Yêu cầu không hợp lệ", ex);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        log.error("Lỗi không mong muốn", ex);
        model.addAttribute("errorMessage", "Đã xảy ra lỗi: " + ex.getMessage());
        return "error";
    }
}
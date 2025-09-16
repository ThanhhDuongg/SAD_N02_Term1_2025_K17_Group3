package com.example.dorm.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;

@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class CustomErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String errorHtml(HttpServletRequest request, Model model) {
        Map<String, Object> attributes = getErrorAttributes(request,
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE));
        model.addAttribute("status", attributes.get("status"));
        model.addAttribute("timestamp", attributes.get("timestamp"));
        model.addAttribute("path", attributes.get("path"));
        Object message = attributes.get("message");
        String resolvedMessage = (message != null && !message.toString().isBlank())
                ? message.toString()
                : "Đã xảy ra lỗi không xác định.";
        model.addAttribute("errorMessage", resolvedMessage);
        return "error";
    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Map<String, Object> attributes = getErrorAttributes(request,
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE,
                        ErrorAttributeOptions.Include.BINDING_ERRORS));
        HttpStatus status = getStatus(request);
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(attributes);
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request, ErrorAttributeOptions options) {
        ServletWebRequest webRequest = new ServletWebRequest(request);
        return this.errorAttributes.getErrorAttributes(webRequest, options);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode instanceof Integer code) {
            try {
                return HttpStatus.valueOf(code);
            } catch (Exception ignored) {
                // Fallback below
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
package com.example.dorm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FaviconController {

    // Map favicon.ico request, nhưng không cần xử lý gì
    @GetMapping("favicon.ico")
    public void favicon() {
        // để trống, Spring Boot sẽ không báo lỗi thiếu static resource nữa
    }
}


package com.example.dorm.service;

import com.example.dorm.model.PasswordResetToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetNotificationService.class);

    private final String applicationBaseUrl;

    public PasswordResetNotificationService(@Value("${app.web.base-url:http://localhost:8080}") String applicationBaseUrl) {
        this.applicationBaseUrl = applicationBaseUrl.endsWith("/")
                ? applicationBaseUrl.substring(0, applicationBaseUrl.length() - 1)
                : applicationBaseUrl;
    }

    public void sendResetInstructions(PasswordResetToken token) {
        String resetLink = applicationBaseUrl + "/reset-password?token=" + token.getToken();
        logger.info("[PASSWORD RESET] Gửi liên kết đặt lại mật khẩu tới {}: {}", token.getUser().getEmail(), resetLink);
    }
}

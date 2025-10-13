package com.example.dorm.service;

import com.example.dorm.model.AuthProvider;
import com.example.dorm.model.PasswordResetToken;
import com.example.dorm.model.User;
import com.example.dorm.repository.PasswordResetTokenRepository;
import com.example.dorm.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);
    private static final Duration TOKEN_TTL = Duration.ofMinutes(30);

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(PasswordResetTokenRepository tokenRepository,
                                UserRepository userRepository,
                                PasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Optional<PasswordResetToken> createTokenForEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        String normalizedEmail = email.trim().toLowerCase();
        Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);
        if (userOpt.isEmpty()) {
            logger.warn("Yêu cầu đặt lại mật khẩu cho email không tồn tại: {}", normalizedEmail);
            return Optional.empty();
        }

        User user = userOpt.get();
        if (user.getProvider() != null && user.getProvider() != AuthProvider.LOCAL) {
            throw new IllegalArgumentException("Tài khoản đăng nhập bằng Google không sử dụng được chức năng quên mật khẩu");
        }

        tokenRepository.deleteByExpiresAtBefore(LocalDateTime.now().minusHours(12));

        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plus(TOKEN_TTL));
        PasswordResetToken saved = tokenRepository.save(token);
        logger.info("Đã tạo token đặt lại mật khẩu cho người dùng {}", user.getUsername());
        return Optional.of(saved);
    }

    public Optional<PasswordResetToken> validateToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        return tokenRepository.findByToken(token)
                .filter(stored -> !stored.isExpired() && !stored.isConsumed());
    }

    @Transactional
    public void resetPassword(String tokenValue, String newPassword) {
        PasswordResetToken token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Token không hợp lệ"));

        if (token.isExpired()) {
            throw new IllegalArgumentException("Token đã hết hạn");
        }
        if (token.isConsumed()) {
            throw new IllegalArgumentException("Token đã được sử dụng");
        }

        User user = token.getUser();
        if (user.getProvider() != null && user.getProvider() != AuthProvider.LOCAL) {
            throw new IllegalArgumentException("Tài khoản đăng nhập bằng Google không hỗ trợ đặt lại mật khẩu");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setConsumedAt(LocalDateTime.now());
        tokenRepository.save(token);
    }
}

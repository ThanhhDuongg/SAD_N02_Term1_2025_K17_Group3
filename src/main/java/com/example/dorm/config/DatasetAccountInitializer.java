package com.example.dorm.config;

import com.example.dorm.model.User;
import com.example.dorm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Normalizes dataset user accounts so that each login uses the email prefix as username
 * and a shared default password. This helps when importing large datasets where
 * usernames/passwords were not prepared according to the system rules.
 */
@Component
public class DatasetAccountInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String defaultPassword;

    public DatasetAccountInitializer(UserRepository userRepository,
                                     PasswordEncoder passwordEncoder,
                                     @Value("${app.dataset.default-password:123}") String defaultPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.defaultPassword = defaultPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            boolean datasetAccount = isDatasetAccount(user);
            if (!datasetAccount) {
                continue;
            }

            boolean changed = false;

            String normalizedUsername = buildUsernameFromEmail(user);
            if (normalizedUsername != null && !normalizedUsername.equals(user.getUsername())) {
                user.setUsername(normalizedUsername);
                changed = true;
            }

            if (!isPasswordAlreadyDefault(user.getPassword())) {
                user.setPassword(passwordEncoder.encode(defaultPassword));
                changed = true;
            }

            if (changed) {
                userRepository.save(user);
            }
        }
    }

    private boolean isDatasetAccount(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        boolean usernameLooksLikeEmail = username != null && username.contains("@");
        boolean passwordNotEncoded = password == null || password.isBlank() || !isBcryptHash(password);
        return usernameLooksLikeEmail || passwordNotEncoded;
    }

    private String buildUsernameFromEmail(User user) {
        String email = user.getEmail();
        if (email == null) {
            return null;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return null;
        }

        String base = email.substring(0, atIndex)
                .replaceAll("[^a-zA-Z0-9._-]", "")
                .toLowerCase();
        if (base.isBlank()) {
            return null;
        }

        String candidate = base;
        int counter = 1;
        while (true) {
            if (candidate.equals(user.getUsername())) {
                return null;
            }
            Optional<User> existing = userRepository.findByUsername(candidate);
            if (existing.isEmpty() || existing.get().getId().equals(user.getId())) {
                return candidate;
            }
            candidate = base + counter++;
        }
    }

    private boolean isPasswordAlreadyDefault(String storedPassword) {
        if (!isBcryptHash(storedPassword)) {
            return false;
        }
        return passwordEncoder.matches(defaultPassword, storedPassword);
    }

    private boolean isBcryptHash(String password) {
        return password != null && (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }
}

package com.example.dorm.service;

import com.example.dorm.model.AuthProvider;
import com.example.dorm.model.Role;
import com.example.dorm.model.RoleName;
import com.example.dorm.model.User;
import com.example.dorm.repository.RoleRepository;
import com.example.dorm.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(UserRepository userRepository,
                                   RoleRepository roleRepository,
                                   PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (!"google".equalsIgnoreCase(registrationId)) {
            throw new OAuth2AuthenticationException("Unsupported OAuth2 provider: " + registrationId);
        }

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException("Google account không cung cấp email");
        }
        email = email.toLowerCase();

        String name = (String) attributes.getOrDefault("name", email);
        String sub = (String) attributes.get("sub");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> registerNewGoogleUser(email, name, sub));

        if (user.getProvider() == null) {
            user.setProvider(AuthProvider.GOOGLE);
        }

        if (user.getProvider() != AuthProvider.GOOGLE) {
            throw new OAuth2AuthenticationException("Email đã được liên kết với tài khoản nội bộ. Vui lòng đăng nhập bằng mật khẩu");
        }

        boolean updated = false;
        if (sub != null && (user.getProviderId() == null || !user.getProviderId().equals(sub))) {
            user.setProviderId(sub);
            updated = true;
        }
        if (name != null && (user.getFullName() == null || !user.getFullName().equals(name))) {
            user.setFullName(name);
            updated = true;
        }
        if (updated) {
            userRepository.save(user);
        }

        Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toSet());

        if (authorities.isEmpty()) {
            Role studentRole = roleRepository.findByName(RoleName.ROLE_STUDENT)
                    .orElseThrow(() -> new OAuth2AuthenticationException("Không tìm thấy quyền ROLE_STUDENT"));
            user.setRoles(Set.of(studentRole));
            userRepository.save(user);
            authorities = Set.of(new SimpleGrantedAuthority(studentRole.getName().name()));
        }

        return new DefaultOAuth2User(authorities, attributes, "email");
    }

    private User registerNewGoogleUser(String email, String name, String providerId) {
        logger.info("Tạo tài khoản mới từ Google cho email {}", email);
        Role defaultRole = roleRepository.findByName(RoleName.ROLE_STUDENT)
                .orElseThrow(() -> new OAuth2AuthenticationException("Không tìm thấy quyền ROLE_STUDENT"));

        User user = new User();
        user.setEmail(email);
        user.setUsername(generateUsernameFromEmail(email));
        user.setFullName(name);
        user.setProvider(AuthProvider.GOOGLE);
        user.setProviderId(providerId);
        user.setEnabled(true);
        user.setLastLoginAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setRoles(Set.of(defaultRole));
        return userRepository.save(user);
    }

    private String generateUsernameFromEmail(String email) {
        String base = email.split("@")[0].replaceAll("[^a-zA-Z0-9._-]", "").toLowerCase();
        if (base.isBlank()) {
            base = "google_user";
        }
        String candidate = base;
        int counter = 1;
        while (true) {
            Optional<User> existing = userRepository.findByUsername(candidate);
            if (existing.isEmpty()) {
                return candidate;
            }
            candidate = base + counter++;
        }
    }
}

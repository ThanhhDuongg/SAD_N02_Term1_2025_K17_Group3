package com.example.dorm.service;

import com.example.dorm.model.User;
import com.example.dorm.model.Role;
import com.example.dorm.model.RoleName;
import com.example.dorm.repository.UserRepository;
import com.example.dorm.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String username, String email, String password, RoleName roleName) {
        String normalizedUsername = trimToNull(username);
        String normalizedEmail = normalizeEmail(email);

        if (normalizedUsername == null) {
            throw new IllegalStateException("Username không được để trống!");
        }

        if (normalizedEmail == null) {
            throw new IllegalStateException("Email không được để trống!");
        }

        if (userRepository.existsByUsername(normalizedUsername)) {
            throw new IllegalStateException("Username đã tồn tại!");
        }

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalStateException("Email đã tồn tại!");
        }

        User user = new User();
        user.setUsername(normalizedUsername);
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(password));

        Role userRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role không tìm thấy: " + roleName));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public List<Role> findAllRoles() {
        return roleRepository.findAll().stream()
                .sorted(Comparator.comparing(role -> role.getName().name()))
                .collect(Collectors.toList());
    }

    public List<User> findUsersByRole(RoleName roleName) {
        return userRepository.findAllByRoles_Name(roleName).stream()
                .sorted(Comparator.comparing(user -> {
                    String display = user.getFullName();
                    if (display == null || display.isBlank()) {
                        display = user.getUsername();
                    }
                    return display.toLowerCase();
                }))
                .collect(Collectors.toList());
    }

    public boolean hasRole(User user, RoleName roleName) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == roleName);
    }

    public void changePassword(User user, String currentPassword, String newPassword) {
        if (user == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng");
        }

        String storedPassword = user.getPassword();
        if (storedPassword == null || storedPassword.isBlank()) {
            throw new IllegalArgumentException("Tài khoản chưa có mật khẩu để xác thực");
        }

        if (!passwordEncoder.matches(currentPassword, storedPassword)) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không chính xác");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User không tìm thấy"));

        String normalizedEmail = normalizeEmail(userDetails.getEmail());
        if (normalizedEmail == null) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        userRepository.findByEmail(normalizedEmail)
                .filter(existing -> !existing.getId().equals(user.getId()))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Email đã được sử dụng bởi tài khoản khác");
                });
        user.setEmail(normalizedEmail);
        user.setEnabled(userDetails.isEnabled());
        user.setFullName(trimToNull(userDetails.getFullName()));
        user.setPhone(trimToNull(userDetails.getPhone()));

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        return userRepository.save(user);
    }

    public User updateProfile(User user, String email, String fullName, String phone) {
        return updateProfile(user, email, fullName, phone, user != null ? user.getAvatarFilename() : null);
    }

    public User updateProfile(User user, String email, String fullName, String phone, String avatarFilename) {
        if (user == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng");
        }

        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail == null) {
            throw new IllegalArgumentException("Email không được để trống");
        }

        userRepository.findByEmail(normalizedEmail)
                .filter(existing -> !existing.getId().equals(user.getId()))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Email đã được sử dụng bởi tài khoản khác");
                });

        user.setEmail(normalizedEmail);
        user.setFullName(trimToNull(fullName));
        user.setPhone(trimToNull(phone));
        user.setAvatarFilename(avatarFilename != null && !avatarFilename.isBlank() ? avatarFilename : null);

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void updateUserRoles(Long id, Set<RoleName> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            throw new IllegalArgumentException("Người dùng phải có ít nhất một vai trò");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User không tìm thấy"));

        Set<Role> roles = roleNames.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Role không tìm thấy: " + roleName)))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        userRepository.save(user);
    }

    public User updateStudentAccount(User user, String email, String fullName, String phone) {
        if (user == null) {
            throw new IllegalArgumentException("Không tìm thấy tài khoản sinh viên");
        }

        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail == null) {
            throw new IllegalArgumentException("Email không được để trống");
        }

        userRepository.findByUsername(normalizedEmail)
                .filter(existing -> !existing.getId().equals(user.getId()))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Username đã được sử dụng bởi tài khoản khác");
                });

        userRepository.findByEmail(normalizedEmail)
                .filter(existing -> !existing.getId().equals(user.getId()))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Email đã được sử dụng bởi tài khoản khác");
                });

        user.setUsername(normalizedEmail);
        user.setEmail(normalizedEmail);
        user.setFullName(trimToNull(fullName));
        user.setPhone(trimToNull(phone));

        return userRepository.save(user);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeEmail(String email) {
        String trimmed = trimToNull(email);
        return trimmed != null ? trimmed.toLowerCase() : null;
    }
}

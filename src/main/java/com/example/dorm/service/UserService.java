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
        if (userRepository.existsByUsername(username)) {
            throw new IllegalStateException("Username đã tồn tại!");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email đã tồn tại!");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
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

        user.setEmail(userDetails.getEmail());
        user.setEnabled(userDetails.isEnabled());
        user.setFullName(userDetails.getFullName());
        user.setPhone(userDetails.getPhone());

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        return userRepository.save(user);
    }

    public User updateProfile(User user, String email, String fullName, String phone) {
        if (user == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email không được để trống");
        }

        userRepository.findByEmail(email)
                .filter(existing -> !existing.getId().equals(user.getId()))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Email đã được sử dụng bởi tài khoản khác");
                });

        user.setEmail(email.trim());
        user.setFullName(fullName != null && !fullName.isBlank() ? fullName.trim() : null);
        user.setPhone(phone != null && !phone.isBlank() ? phone.trim() : null);

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
}
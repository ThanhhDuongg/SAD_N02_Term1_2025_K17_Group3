package com.example.dorm.controller;

import com.example.dorm.dto.ChangePasswordForm;
import com.example.dorm.dto.ProfileForm;
import com.example.dorm.model.RoleName;
import com.example.dorm.model.Student;
import com.example.dorm.model.User;
import com.example.dorm.service.StudentService;
import com.example.dorm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final UserService userService;
    private final StudentService studentService;

    public AccountController(UserService userService, StudentService studentService) {
        this.userService = userService;
        this.studentService = studentService;
    }

    @GetMapping("/password")
    public String showChangePasswordForm(Model model) {
        if (!model.containsAttribute("changePasswordForm")) {
            model.addAttribute("changePasswordForm", new ChangePasswordForm());
        }
        return "auth/change-password";
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        boolean isStudent = userService.hasRole(user, RoleName.ROLE_STUDENT);

        prepareProfileModel(model, user, isStudent);
        return "account/profile";
    }

    @PostMapping("/password")
    public String changePassword(@Valid @ModelAttribute("changePasswordForm") ChangePasswordForm form,
                                 BindingResult bindingResult,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/change-password";
        }

        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Mật khẩu xác nhận không khớp");
            return "auth/change-password";
        }

        if (authentication == null || authentication.getName() == null) {
            bindingResult.reject("authentication.missing", "Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.");
            return "auth/change-password";
        }

        return userService.findByUsername(authentication.getName())
                .map(user -> handlePasswordChange(user, form, redirectAttributes, bindingResult))
                .orElseGet(() -> {
                    bindingResult.reject("user.notFound", "Không tìm thấy thông tin người dùng");
                    return "auth/change-password";
                });
    }

    private String handlePasswordChange(User user,
                                        ChangePasswordForm form,
                                        RedirectAttributes redirectAttributes,
                                        BindingResult bindingResult) {
        try {
            userService.changePassword(user, form.getCurrentPassword(), form.getNewPassword());
            redirectAttributes.addFlashAttribute("message", "Đổi mật khẩu thành công");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/account/password";
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("currentPassword", "password.invalid", ex.getMessage());
            return "auth/change-password";
        } catch (Exception ex) {
            bindingResult.reject("password.change.error", ex.getMessage());
            return "auth/change-password";
        }
    }

    @PostMapping("/profile")
    @Transactional
    public String updateProfile(@Valid @ModelAttribute("profileForm") ProfileForm form,
                                BindingResult bindingResult,
                                Authentication authentication,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser(authentication);
        boolean isStudent = userService.hasRole(user, RoleName.ROLE_STUDENT);

        if (bindingResult.hasErrors()) {
            prepareProfileModel(model, user, isStudent);
            return "account/profile";
        }

        try {
            if (isStudent) {
                Student student = studentService.findByUsername(user.getUsername())
                        .orElseThrow(() -> new IllegalStateException("Không tìm thấy thông tin sinh viên"));
                studentService.updateContactInfo(student.getId(), form.getPhone(), form.getEmail(), form.getAddress());
            }
            userService.updateProfile(user, form.getEmail(), form.getFullName(), form.getPhone());

            redirectAttributes.addFlashAttribute("message", "Cập nhật thông tin cá nhân thành công");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/account/profile";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            String message = ex.getMessage() != null ? ex.getMessage() : "Không thể cập nhật thông tin";
            if (message.toLowerCase(Locale.ROOT).contains("email")) {
                bindingResult.rejectValue("email", "profile.email", message);
            } else {
                bindingResult.reject("profile.update.error", message);
            }
            prepareProfileModel(model, user, isStudent);
            return "account/profile";
        }
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.");
        }
        return userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy thông tin người dùng"));
    }

    private void prepareProfileModel(Model model, User user, boolean isStudent) {
        Student student = null;
        if (isStudent) {
            student = studentService.findByUsername(user.getUsername())
                    .orElseThrow(() -> new IllegalStateException("Không tìm thấy thông tin sinh viên"));
        }

        if (!model.containsAttribute("profileForm")) {
            ProfileForm form = new ProfileForm();
            form.setUsername(user.getUsername());
            form.setFullName(user.getFullName());
            String preferredEmail = user.getEmail();
            if (student != null && student.getEmail() != null && !student.getEmail().isBlank()) {
                preferredEmail = student.getEmail();
            }
            form.setEmail(preferredEmail);
            form.setPhone(student != null && student.getPhone() != null ? student.getPhone() : user.getPhone());
            if (student != null) {
                form.setAddress(student.getAddress());
            }
            model.addAttribute("profileForm", form);
        }

        model.addAttribute("isStudent", isStudent);
        model.addAttribute("canEditFullName", !isStudent);
        if (student != null) {
            model.addAttribute("studentProfile", student);
        }
    }
}

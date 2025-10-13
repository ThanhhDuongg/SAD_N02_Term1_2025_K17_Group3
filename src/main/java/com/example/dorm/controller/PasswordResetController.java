package com.example.dorm.controller;

import com.example.dorm.model.PasswordResetToken;
import com.example.dorm.service.PasswordResetNotificationService;
import com.example.dorm.service.PasswordResetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private final PasswordResetNotificationService notificationService;

    public PasswordResetController(PasswordResetService passwordResetService,
                                   PasswordResetNotificationService notificationService) {
        this.passwordResetService = passwordResetService;
        this.notificationService = notificationService;
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new ForgotPasswordForm());
        }
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@ModelAttribute("form") ForgotPasswordForm form,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes) {
        if (!StringUtils.hasText(form.getEmail())) {
            bindingResult.rejectValue("email", "email.required", "Vui lòng nhập email đăng ký");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.form", bindingResult);
            redirectAttributes.addFlashAttribute("form", form);
            return "redirect:/forgot-password";
        }

        try {
            passwordResetService.createTokenForEmail(form.getEmail())
                    .ifPresent(notificationService::sendResetInstructions);
            redirectAttributes.addFlashAttribute("message",
                    "Nếu email tồn tại, chúng tôi đã gửi hướng dẫn đặt lại mật khẩu. Vui lòng kiểm tra hộp thư.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }

        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (!StringUtils.hasText(token)) {
            redirectAttributes.addFlashAttribute("message", "Token không hợp lệ");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/forgot-password";
        }

        PasswordResetToken validToken = passwordResetService.validateToken(token)
                .orElse(null);
        if (validToken == null) {
            redirectAttributes.addFlashAttribute("message", "Liên kết đặt lại mật khẩu không hợp lệ hoặc đã hết hạn");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/forgot-password";
        }

        if (!model.containsAttribute("form")) {
            ResetPasswordForm form = new ResetPasswordForm();
            form.setToken(token);
            model.addAttribute("form", form);
        }
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@ModelAttribute("form") ResetPasswordForm form,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {
        if (!StringUtils.hasText(form.getToken())) {
            bindingResult.reject("token.invalid", "Token không hợp lệ");
        }
        if (!StringUtils.hasText(form.getPassword())) {
            bindingResult.rejectValue("password", "password.required", "Vui lòng nhập mật khẩu mới");
        }
        if (!StringUtils.hasText(form.getConfirmPassword()) ||
                !form.getPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Mật khẩu xác nhận không khớp");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.form", bindingResult);
            redirectAttributes.addFlashAttribute("form", form);
            return "redirect:/reset-password?token=" + form.getToken();
        }

        try {
            passwordResetService.resetPassword(form.getToken(), form.getPassword());
            redirectAttributes.addFlashAttribute("message", "Đặt lại mật khẩu thành công. Vui lòng đăng nhập lại.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/forgot-password";
        }
    }

    public static class ForgotPasswordForm {
        @NotBlank
        @Email
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class ResetPasswordForm {
        @NotBlank
        private String token;

        @NotBlank
        @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
        private String password;

        @NotBlank
        private String confirmPassword;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }
}

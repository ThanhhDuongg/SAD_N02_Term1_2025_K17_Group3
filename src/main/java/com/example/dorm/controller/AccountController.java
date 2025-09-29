package com.example.dorm.controller;

import com.example.dorm.dto.ChangePasswordForm;
import com.example.dorm.model.User;
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

@Controller
@RequestMapping("/account")
public class AccountController {

    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/password")
    public String showChangePasswordForm(Model model) {
        if (!model.containsAttribute("changePasswordForm")) {
            model.addAttribute("changePasswordForm", new ChangePasswordForm());
        }
        return "auth/change-password";
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
}

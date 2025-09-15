package com.example.dorm.controller;

import com.example.dorm.dto.StudentRegistrationForm;
import com.example.dorm.model.Student;
import com.example.dorm.model.User;
import com.example.dorm.model.RoleName;
import com.example.dorm.service.StudentService;
import com.example.dorm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (isAuthenticated(authentication)) {
            return redirectByRole(authentication);
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Authentication authentication, Model model) {
        if (isAuthenticated(authentication)) {
            return redirectByRole(authentication);
        }
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new StudentRegistrationForm());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerStudent(@Valid @ModelAttribute("registrationForm") StudentRegistrationForm form,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Mật khẩu xác nhận không khớp");
        }

        studentService.findByCode(form.getStudentCode()).ifPresentOrElse(student -> {
            validateStudentRegistration(form, student, bindingResult);
            if (!bindingResult.hasErrors()) {
                try {
                    User user = userService.createUser(form.getUsername(), form.getEmail(), form.getPassword(), RoleName.ROLE_STUDENT);
                    student.setUser(user);
                    studentService.saveStudent(student);
                } catch (IllegalStateException ex) {
                    bindingResult.reject("registration.error", ex.getMessage());
                }
            }
        }, () -> bindingResult.rejectValue("studentCode", "student.notFound", "Không tìm thấy sinh viên với mã này"));

        if (bindingResult.hasErrors()) {
            model.addAttribute("registrationForm", form);
            return "auth/register";
        }

        redirectAttributes.addFlashAttribute("message", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        return "redirect:/login";
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private String redirectByRole(Authentication authentication) {
        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_STUDENT"));
        if (isStudent) {
            return "redirect:/student/dashboard";
        }
        return "redirect:/dashboard";
    }

    private void validateStudentRegistration(StudentRegistrationForm form,
                                             Student student,
                                             BindingResult bindingResult) {
        if (student.getEmail() != null && !student.getEmail().equalsIgnoreCase(form.getEmail())) {
            bindingResult.rejectValue("email", "email.mismatch", "Email không trùng khớp với hồ sơ sinh viên");
        }
        if (student.getUser() != null) {
            bindingResult.rejectValue("studentCode", "student.hasAccount", "Sinh viên đã có tài khoản đăng nhập");
        }
    }
}

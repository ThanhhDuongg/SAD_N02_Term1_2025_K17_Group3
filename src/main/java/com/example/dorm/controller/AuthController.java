package com.example.dorm.controller;

import com.example.dorm.dto.StudentAccountCredentials;
import com.example.dorm.dto.StudentRegistrationForm;
import com.example.dorm.model.Student;
import com.example.dorm.service.StudentService;
import jakarta.validation.Valid;
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

    private final StudentService studentService;
    public AuthController(StudentService studentService) {
        this.studentService = studentService;
    }

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
        Student student = studentService.findByCode(form.getStudentCode())
                .orElse(null);

        if (student == null) {
            bindingResult.rejectValue("studentCode", "student.notFound", "Không tìm thấy sinh viên với mã này");
        } else {
            validateStudentRegistration(form, student, bindingResult);
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("registrationForm", form);
            return "auth/register";
        }

        try {
            StudentAccountCredentials credentials = studentService.registerStudentAccount(student, form.getEmail());
            redirectAttributes.addFlashAttribute("message", "Hệ thống đã tạo tài khoản và gửi thông tin đăng nhập qua email của bạn.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            redirectAttributes.addFlashAttribute("generatedUsername", credentials.username());
            redirectAttributes.addFlashAttribute("generatedPassword", credentials.password());
        } catch (IllegalStateException | IllegalArgumentException ex) {
            bindingResult.reject("registration.error", ex.getMessage());
            model.addAttribute("registrationForm", form);
            return "auth/register";
        }

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

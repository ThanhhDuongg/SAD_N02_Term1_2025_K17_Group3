package com.example.dorm.controller;

import com.example.dorm.dto.UserForm;
import com.example.dorm.model.Role;
import com.example.dorm.model.RoleName;
import com.example.dorm.model.User;
import com.example.dorm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UserManagementController {

    private final UserService userService;

    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAllUsers();
        List<Role> roles = userService.findAllRoles();

        model.addAttribute("users", users);
        model.addAttribute("allRoles", roles);
        return "users/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("userForm")) {
            UserForm form = new UserForm();
            form.setRole(RoleName.ROLE_STAFF);
            model.addAttribute("userForm", form);
        }
        model.addAttribute("roles", Arrays.stream(RoleName.values())
                .filter(roleName -> roleName != RoleName.ROLE_STUDENT)
                .collect(Collectors.toList()));
        return "users/form";
    }

    @PostMapping
    public String createUser(@Valid @ModelAttribute("userForm") UserForm form,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        validatePasswords(form, bindingResult);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userForm", bindingResult);
            redirectAttributes.addFlashAttribute("userForm", form);
            return "redirect:/users/new";
        }

        try {
            userService.createUser(form.getUsername(), form.getEmail(), form.getPassword(), form.getRole());
            redirectAttributes.addFlashAttribute("message", "Tạo tài khoản nhân sự thành công");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/users";
        } catch (IllegalStateException ex) {
            String message = ex.getMessage();
            if (message != null && message.toLowerCase(Locale.ROOT).contains("email")) {
                bindingResult.rejectValue("email", "user.email.exists", message);
            } else {
                bindingResult.rejectValue("username", "user.exists", message);
            }
        } catch (RuntimeException ex) {
            bindingResult.reject("user.create.error", ex.getMessage());
        }

        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userForm", bindingResult);
        redirectAttributes.addFlashAttribute("userForm", form);
        return "redirect:/users/new";
    }

    @PostMapping("/{id}/roles")
    public String updateRoles(@PathVariable Long id,
                              @RequestParam(value = "roles", required = false) List<String> roleNames,
                              RedirectAttributes redirectAttributes) {
        try {
            Set<RoleName> selectedRoles = convertToRoleNames(roleNames);
            userService.updateUserRoles(id, selectedRoles);
            redirectAttributes.addFlashAttribute("message", "Cập nhật phân quyền thành công");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/users";
    }

    private void validatePasswords(UserForm form, BindingResult bindingResult) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Mật khẩu xác nhận không khớp");
        }
    }

    private Set<RoleName> convertToRoleNames(List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một vai trò");
        }

        EnumSet<RoleName> roles = EnumSet.noneOf(RoleName.class);
        for (String roleName : roleNames) {
            try {
                roles.add(RoleName.valueOf(roleName.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Vai trò không hợp lệ: " + roleName);
            }
        }
        return roles;
    }
}

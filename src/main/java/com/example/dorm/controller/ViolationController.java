package com.example.dorm.controller;

import com.example.dorm.model.Violation;
import com.example.dorm.service.ViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/violations")
public class ViolationController {

    @Autowired
    private ViolationService violationService;

    @GetMapping
    public String listViolations(@RequestParam(value = "studentId", required = false) Long studentId,
                                 @RequestParam(value = "roomId", required = false) Long roomId,
                                 Model model) {
        List<Violation> violations;
        if (studentId != null) {
            violations = violationService.getViolationsByStudent(studentId);
        } else if (roomId != null) {
            violations = violationService.getViolationsByRoom(roomId);
        } else {
            violations = violationService.getAllViolations();
        }
        model.addAttribute("violations", violations);
        return "violations/list";
    }

    @GetMapping("/new")
    public String newViolationForm(Model model) {
        model.addAttribute("violation", new Violation());
        return "violations/form";
    }

    @PostMapping
    public String createViolation(@ModelAttribute Violation violation, RedirectAttributes redirectAttributes) {
        violationService.recordViolation(violation);
        redirectAttributes.addFlashAttribute("message", "Đã ghi nhận vi phạm");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        return "redirect:/violations";
    }
}

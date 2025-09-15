package com.example.dorm.controller;

import com.example.dorm.model.Violation;
import com.example.dorm.model.Student;
import com.example.dorm.model.Room;
import com.example.dorm.service.ViolationService;
import com.example.dorm.service.StudentService;
import com.example.dorm.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/violations")
public class ViolationController {

    private static final List<String> SEVERITY_LEVELS = List.of("LOW", "MEDIUM", "HIGH");

    @Autowired
    private ViolationService violationService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private RoomService roomService;

    @ModelAttribute("severityLevels")
    public List<String> severityLevels() {
        return SEVERITY_LEVELS;
    }

    @ModelAttribute("students")
    public List<Student> students() {
        return studentService.getAllStudents();
    }

    @ModelAttribute("rooms")
    public List<Room> rooms() {
        return roomService.getAllRooms();
    }

    @GetMapping
    public String listViolations(@RequestParam(value = "studentId", required = false) Long studentId,
                                 @RequestParam(value = "roomId", required = false) Long roomId,
                                 @RequestParam(value = "severity", required = false) String severity,
                                 Model model) {
        List<Violation> violations;
        if (studentId != null) {
            violations = violationService.getViolationsByStudent(studentId);
        } else if (roomId != null) {
            violations = violationService.getViolationsByRoom(roomId);
        } else {
            violations = violationService.getAllViolations();
        }

        if (severity != null && !severity.isBlank()) {
            String severityUpper = severity.toUpperCase();
            violations = violations.stream()
                    .filter(v -> severityUpper.equalsIgnoreCase(v.getSeverity()))
                    .collect(Collectors.toList());
            model.addAttribute("selectedSeverity", severityUpper);
        } else {
            model.addAttribute("selectedSeverity", "");
        }

        Map<String, Long> roomSummary = violationService.countByRoom();
        Map<String, Long> studentSummary = violationService.countByStudent();
        Map<String, Long> severitySummary = violationService.countBySeverity();

        model.addAttribute("violations", violations);
        model.addAttribute("selectedStudentId", studentId);
        model.addAttribute("selectedRoomId", roomId);
        model.addAttribute("roomSummary", roomSummary);
        model.addAttribute("studentSummary", studentSummary);
        model.addAttribute("severitySummary", severitySummary);
        return "violations/list";
    }

    @GetMapping("/new")
    public String newViolationForm(Model model) {
        if (!model.containsAttribute("violation")) {
            model.addAttribute("violation", new Violation());
        }
        return "violations/form";
    }

    @PostMapping
    public String createViolation(@RequestParam("studentId") Long studentId,
                                  @RequestParam(value = "roomId", required = false) Long roomId,
                                  @RequestParam("description") String description,
                                  @RequestParam("severity") String severity,
                                  RedirectAttributes redirectAttributes) {
        try {
            Violation violation = new Violation();
            Student student = studentService.getStudent(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên"));
            violation.setStudent(student);

            if (roomId != null) {
                roomService.getRoom(roomId)
                        .ifPresent(violation::setRoom);
            }

            violation.setDescription(description);
            violation.setSeverity(severity);
            violationService.recordViolation(violation);

            redirectAttributes.addFlashAttribute("message", "Đã ghi nhận vi phạm");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Không thể ghi nhận vi phạm: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/violations";
    }
}

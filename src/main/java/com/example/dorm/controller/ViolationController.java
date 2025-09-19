package com.example.dorm.controller;

import com.example.dorm.model.Violation;
import com.example.dorm.model.Student;
import com.example.dorm.model.Room;
import com.example.dorm.service.StudentService;
import com.example.dorm.service.RoomService;
import com.example.dorm.service.UserService;
import com.example.dorm.service.ViolationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Controller
@RequestMapping("/violations")
public class ViolationController {

    private static final List<String> SEVERITY_LEVELS = List.of("LOW", "MEDIUM", "HIGH");
    private static final List<String> VIOLATION_TYPES = List.of(
            "DORM_RULE", "ACADEMIC", "SAFETY", "FINANCE", "OTHER");

    private final ViolationService violationService;
    private final StudentService studentService;
    private final RoomService roomService;
    private final UserService userService;

    public ViolationController(ViolationService violationService,
                               StudentService studentService,
                               RoomService roomService,
                               UserService userService) {
        this.violationService = violationService;
        this.studentService = studentService;
        this.roomService = roomService;
        this.userService = userService;
    }

    @ModelAttribute("severityLevels")
    public List<String> severityLevels() {
        return SEVERITY_LEVELS;
    }

    @ModelAttribute("violationTypes")
    public List<String> violationTypes() {
        return VIOLATION_TYPES;
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
                                 @RequestParam(value = "type", required = false) String type,
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

        if (type != null && !type.isBlank()) {
            String typeUpper = type.toUpperCase();
            violations = violations.stream()
                    .filter(v -> typeUpper.equalsIgnoreCase(v.getType()))
                    .collect(Collectors.toList());
            model.addAttribute("selectedType", typeUpper);
        } else {
            model.addAttribute("selectedType", "");
        }

        Map<String, Long> roomSummary = violationService.countByRoom();
        Map<String, Long> studentSummary = violationService.countByStudent();
        Map<String, Long> severitySummary = violationService.countBySeverity();
        Map<String, Long> typeSummary = violationService.countByType();

        model.addAttribute("violations", violations);
        model.addAttribute("selectedStudentId", studentId);
        model.addAttribute("selectedRoomId", roomId);
        model.addAttribute("roomSummary", roomSummary);
        model.addAttribute("studentSummary", studentSummary);
        model.addAttribute("severitySummary", severitySummary);
        model.addAttribute("typeSummary", typeSummary);
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
                                  @RequestParam("violationType") String violationType,
                                  @RequestParam(value = "date", required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate violationDate,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        Violation violation = new Violation();
        violation.setDescription(description);
        violation.setSeverity(severity);
        violation.setType(violationType);
        if (violationDate != null) {
            violation.setDate(violationDate);
        }

        try {
            Student student = studentService.getStudent(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên"));
            violation.setStudent(student);

            if (roomId != null) {
                roomService.getRoom(roomId)
                        .ifPresent(violation::setRoom);
            }

            if (authentication != null) {
                userService.findByUsername(authentication.getName())
                        .ifPresent(violation::setCreatedBy);
            }
            violationService.createViolation(violation);

            redirectAttributes.addFlashAttribute("message", "Đã ghi nhận vi phạm");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/violations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Không thể ghi nhận vi phạm: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("violation", violation);
            return "redirect:/violations/new";
        }
    }
}

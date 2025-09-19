package com.example.dorm.controller;

import com.example.dorm.model.Contract;
import com.example.dorm.model.Fee;
import com.example.dorm.model.MaintenanceRequest;
import com.example.dorm.model.Student;
import com.example.dorm.model.Violation;
import com.example.dorm.service.ContractService;
import com.example.dorm.service.FeeService;
import com.example.dorm.service.MaintenanceRequestService;
import com.example.dorm.service.StudentService;
import com.example.dorm.service.ViolationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentPortalController {

    private static final List<String> REQUEST_TYPES = List.of("MAINTENANCE", "INCIDENT", "ROOM_TRANSFER");

    private final StudentService studentService;
    private final ContractService contractService;
    private final FeeService feeService;
    private final MaintenanceRequestService maintenanceRequestService;
    private final ViolationService violationService;

    public StudentPortalController(StudentService studentService,
                                   ContractService contractService,
                                   FeeService feeService,
                                   MaintenanceRequestService maintenanceRequestService,
                                   ViolationService violationService) {
        this.studentService = studentService;
        this.contractService = contractService;
        this.feeService = feeService;
        this.maintenanceRequestService = maintenanceRequestService;
        this.violationService = violationService;
    }

    @ModelAttribute("requestTypes")
    public List<String> requestTypes() {
        return REQUEST_TYPES;
    }

    @GetMapping("/dashboard")
    public String studentDashboard(Model model, Authentication authentication) {
        Student student = requireAuthenticatedStudent(authentication);
        model.addAttribute("student", student);

        Contract currentContract = contractService.findLatestContractByStudentId(student.getId());
        model.addAttribute("currentContract", currentContract);

        List<Fee> unpaidFees = feeService.getUnpaidFeesByStudent(student.getId());
        model.addAttribute("unpaidFees", unpaidFees);
        model.addAttribute("unpaidCount", unpaidFees.size());

        List<MaintenanceRequest> maintenanceRequests = maintenanceRequestService.getRequestsByStudent(student.getId());
        model.addAttribute("maintenanceRequests", maintenanceRequests);

        List<Violation> violations = violationService.getViolationsByStudent(student.getId());
        model.addAttribute("violations", violations);
        model.addAttribute("violationCount", violations.size());

        Optional<Violation> latestHighSeverity = violations.stream()
                .filter(violation -> "HIGH".equalsIgnoreCase(violation.getSeverity()))
                .max(Comparator.comparing(
                        Violation::getDate,
                        Comparator.nullsLast(Comparator.naturalOrder())));
        model.addAttribute("hasHighSeverityViolation", latestHighSeverity.isPresent());
        model.addAttribute("latestHighSeverityViolation", latestHighSeverity.orElse(null));

        List<String> notifications = buildNotifications(unpaidFees, maintenanceRequests, violations);
        model.addAttribute("notifications", notifications);
        model.addAttribute("hasNotifications", !notifications.isEmpty());

        return "student/dashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication authentication) {
        Student student = requireAuthenticatedStudent(authentication);
        model.addAttribute("student", student);
        return "student/profile";
    }

    @GetMapping("/contracts")
    public String viewContracts(Model model, Authentication authentication) {
        Student student = requireAuthenticatedStudent(authentication);
        model.addAttribute("student", student);
        model.addAttribute("contracts", contractService.getContractsByStudent(student.getId()));
        return "student/contracts";
    }

    @GetMapping("/fees")
    public String viewFees(Model model, Authentication authentication) {
        Student student = requireAuthenticatedStudent(authentication);
        model.addAttribute("student", student);
        model.addAttribute("fees", feeService.getFeesByStudent(student.getId()));
        return "student/fees";
    }

    @GetMapping("/maintenance/new")
    public String newMaintenanceRequest(Model model, Authentication authentication) {
        Student student = requireAuthenticatedStudent(authentication);
        model.addAttribute("student", student);
        model.addAttribute("maintenanceRequest", new MaintenanceRequest());
        return "student/maintenance-form";
    }

    @GetMapping("/requests")
    public String viewRequests(Model model, Authentication authentication) {
        Student student = requireAuthenticatedStudent(authentication);
        model.addAttribute("student", student);
        model.addAttribute("requests", maintenanceRequestService.getRequestsByStudent(student.getId()));
        return "student/requests";
    }

    @PostMapping("/maintenance")
    public String createMaintenanceRequest(@ModelAttribute MaintenanceRequest request,
                                           Authentication authentication,
                                           RedirectAttributes redirectAttributes) {
        try {
            Student student = requireAuthenticatedStudent(authentication);
            request.setStudent(student);
            request.setRoom(student.getRoom());
            if (request.getRequestType() == null || request.getRequestType().isBlank()) {
                request.setRequestType("MAINTENANCE");
            }
            maintenanceRequestService.createRequest(request);
            redirectAttributes.addFlashAttribute("message", "Yêu cầu của bạn đã được gửi thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/student/requests";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Lỗi khi tạo yêu cầu: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/student/dashboard";
        }
    }

    @GetMapping("/violations")
    public String viewViolations(Model model, Authentication authentication) {
        Student student = requireAuthenticatedStudent(authentication);
        model.addAttribute("student", student);
        model.addAttribute("violations", violationService.getViolationsByStudent(student.getId()));
        return "student/violations";
    }

    private Optional<Student> getAuthenticatedStudent(Authentication authentication) {
        if (authentication == null) {
            return Optional.empty();
        }
        return studentService.findByUsername(authentication.getName());
    }

    private Student requireAuthenticatedStudent(Authentication authentication) {
        return getAuthenticatedStudent(authentication)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy thông tin sinh viên"));
    }

    private List<String> buildNotifications(List<Fee> unpaidFees,
                                            List<MaintenanceRequest> requests,
                                            List<Violation> violations) {
        List<String> notifications = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Fee fee : unpaidFees) {
            if (fee.getDueDate() != null) {
                long days = ChronoUnit.DAYS.between(today, fee.getDueDate());
                if (days < 0) {
                    notifications.add("Phí " + fee.getType() + " đã quá hạn " + Math.abs(days) + " ngày (hạn " + fee.getDueDate() + ")");
                } else if (days <= 5) {
                    notifications.add("Phí " + fee.getType() + " đến hạn trong " + days + " ngày (" + fee.getDueDate() + ")");
                }
            }
        }

        boolean hasPending = requests.stream()
                .anyMatch(request -> "PENDING".equalsIgnoreCase(request.getStatus()));
        if (hasPending) {
            notifications.add("Bạn có yêu cầu đang chờ xử lý. Vui lòng theo dõi phản hồi từ ban quản lý.");
        }

        requests.stream()
                .filter(request -> request.getUpdatedAt() != null && "COMPLETED".equalsIgnoreCase(request.getStatus()))
                .max(Comparator.comparing(MaintenanceRequest::getUpdatedAt))
                .ifPresent(request -> notifications.add(
                        "Yêu cầu " + formatRequestType(request) + " của bạn đã được xử lý"
                                + buildResolutionSuffix(request)));

        requests.stream()
                .filter(request -> request.getUpdatedAt() != null && "REJECTED".equalsIgnoreCase(request.getStatus()))
                .max(Comparator.comparing(MaintenanceRequest::getUpdatedAt))
                .ifPresent(request -> notifications.add(
                        "Yêu cầu " + formatRequestType(request) + " đã bị từ chối"
                                + buildResolutionSuffix(request)));

        violations.stream()
                .filter(violation -> "HIGH".equalsIgnoreCase(violation.getSeverity()))
                .findFirst()
                .ifPresent(violation -> notifications.add("Cảnh báo: Vi phạm mức nghiêm trọng" +
                        (violation.getType() != null ? " - " + violation.getType().replace('_', ' ') : "") +
                        " (" + violation.getDate() + ")"));

        return notifications;
    }

    private String formatRequestType(MaintenanceRequest request) {
        if (request.getRequestType() == null || request.getRequestType().isBlank()) {
            return "hỗ trợ";
        }
        String normalized = request.getRequestType().replace('_', ' ').toLowerCase();
        return Character.toUpperCase(normalized.charAt(0)) + normalized.substring(1);
    }

    private String buildResolutionSuffix(MaintenanceRequest request) {
        StringBuilder builder = new StringBuilder();
        if (request.getHandledBy() != null) {
            builder.append(" bởi ").append(request.getHandledBy().getUsername());
        }
        if (request.getResolutionNotes() != null && !request.getResolutionNotes().isBlank()) {
            builder.append(" – ").append(request.getResolutionNotes());
        }
        return builder.toString();
    }
}

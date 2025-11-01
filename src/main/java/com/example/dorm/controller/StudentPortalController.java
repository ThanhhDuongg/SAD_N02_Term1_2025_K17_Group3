package com.example.dorm.controller;

import com.example.dorm.model.Contract;
import com.example.dorm.model.DormRegistrationPeriod;
import com.example.dorm.model.DormRegistrationRequest;
import com.example.dorm.model.DormRegistrationStatus;
import com.example.dorm.model.Fee;
import com.example.dorm.model.MaintenanceRequest;
import com.example.dorm.model.Student;
import com.example.dorm.model.StudentNotification;
import com.example.dorm.model.Violation;
import com.example.dorm.service.ContractService;
import com.example.dorm.service.DormRegistrationPeriodService;
import com.example.dorm.service.DormRegistrationRequestService;
import com.example.dorm.service.FeeService;
import com.example.dorm.service.MaintenanceRequestService;
import com.example.dorm.service.StudentNotificationService;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentPortalController {

    private static final List<String> REQUEST_TYPES = List.of("MAINTENANCE", "INCIDENT", "ROOM_TRANSFER");
    private static final List<String> ROOM_TYPE_OPTIONS = List.of(
            "Phòng 2 người",
            "Phòng 4 người",
            "Phòng 6 người",
            "Phòng 8 người"
    );

    private final StudentService studentService;
    private final ContractService contractService;
    private final FeeService feeService;
    private final MaintenanceRequestService maintenanceRequestService;
    private final ViolationService violationService;
    private final DormRegistrationRequestService dormRegistrationRequestService;
    private final DormRegistrationPeriodService dormRegistrationPeriodService;
    private final StudentNotificationService studentNotificationService;

    public StudentPortalController(StudentService studentService,
                                   ContractService contractService,
                                   FeeService feeService,
                                   MaintenanceRequestService maintenanceRequestService,
                                   ViolationService violationService,
                                   DormRegistrationRequestService dormRegistrationRequestService,
                                   DormRegistrationPeriodService dormRegistrationPeriodService,
                                   StudentNotificationService studentNotificationService) {
        this.studentService = studentService;
        this.contractService = contractService;
        this.feeService = feeService;
        this.maintenanceRequestService = maintenanceRequestService;
        this.violationService = violationService;
        this.dormRegistrationRequestService = dormRegistrationRequestService;
        this.dormRegistrationPeriodService = dormRegistrationPeriodService;
        this.studentNotificationService = studentNotificationService;
    }

    @ModelAttribute("requestTypes")
    public List<String> requestTypes() {
        return REQUEST_TYPES;
    }

    @ModelAttribute("roomTypeOptions")
    public List<String> roomTypeOptions() {
        return ROOM_TYPE_OPTIONS;
    }

    @ModelAttribute("registrationStatuses")
    public DormRegistrationStatus[] registrationStatuses() {
        return DormRegistrationStatus.values();
    }

    @ModelAttribute("activeRegistrationPeriod")
    public DormRegistrationPeriod activeRegistrationPeriod() {
        return dormRegistrationPeriodService.getOpenPeriod().orElse(null);
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

        List<DormRegistrationRequest> registrationRequests = dormRegistrationRequestService.findByStudent(student.getId());
        model.addAttribute("registrationRequests", registrationRequests);

        DormRegistrationPeriod activePeriod = dormRegistrationPeriodService.getOpenPeriod().orElse(null);
        model.addAttribute("activeRegistrationPeriod", activePeriod);

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

        List<StudentNotification> directNotifications = studentNotificationService.consumeUnread(student.getId());
        List<String> notifications = buildNotifications(unpaidFees, maintenanceRequests, registrationRequests, violations, activePeriod, directNotifications);
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

    @GetMapping("/registrations")
    public String viewRegistrationRequests(Model model, Authentication authentication) {
        Student student = requireAuthenticatedStudent(authentication);
        model.addAttribute("student", student);
        DormRegistrationPeriod activePeriod = dormRegistrationPeriodService.getOpenPeriod().orElse(null);
        model.addAttribute("activeRegistrationPeriod", activePeriod);
        boolean alreadySubmitted = activePeriod != null &&
                dormRegistrationRequestService.hasSubmissionInPeriod(student.getId(), activePeriod.getId());
        model.addAttribute("alreadySubmittedInActivePeriod", alreadySubmitted);
        model.addAttribute("registrationRequests", dormRegistrationRequestService.findByStudent(student.getId()));
        return "student/registration-list";
    }

    @GetMapping("/registrations/new")
    public String newRegistrationRequest(Model model,
                                         Authentication authentication,
                                         RedirectAttributes redirectAttributes) {
        Student student = requireAuthenticatedStudent(authentication);
        DormRegistrationPeriod activePeriod = dormRegistrationPeriodService.getOpenPeriod().orElse(null);
        if (activePeriod == null) {
            redirectAttributes.addFlashAttribute("message", "Hiện chưa có đợt đăng ký KTX nào đang mở.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-warning");
            return "redirect:/student/registrations";
        }

        if (dormRegistrationRequestService.hasSubmissionInPeriod(student.getId(), activePeriod.getId())) {
            redirectAttributes.addFlashAttribute("message", "Bạn đã gửi đăng ký trong đợt hiện tại. Vui lòng chờ kết quả.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-info");
            return "redirect:/student/registrations";
        }

        model.addAttribute("student", student);
        model.addAttribute("activeRegistrationPeriod", activePeriod);
        DormRegistrationRequest request = new DormRegistrationRequest();
        request.setDesiredRoomType(student.getRoom() != null ? student.getRoom().getType() : null);
        model.addAttribute("registrationRequest", request);
        return "student/registration-form";
    }

    @PostMapping("/registrations")
    public String createRegistrationRequest(@ModelAttribute("registrationRequest") DormRegistrationRequest request,
                                            Authentication authentication,
                                            RedirectAttributes redirectAttributes) {
        try {
            Student student = requireAuthenticatedStudent(authentication);
            dormRegistrationRequestService.submitRequest(student, request);
            redirectAttributes.addFlashAttribute("message", "Đã gửi yêu cầu đăng ký ký túc xá thành công.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/student/registrations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Không thể gửi yêu cầu: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/student/registrations";
        }
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
                                            List<DormRegistrationRequest> registrationRequests,
                                            List<Violation> violations,
                                            DormRegistrationPeriod activePeriod,
                                            List<StudentNotification> directNotifications) {
        List<String> notifications = new ArrayList<>();
        LocalDate today = LocalDate.now();

        if (directNotifications != null) {
            directNotifications.forEach(notification -> {
                String title = notification.getTitle();
                if (title != null && !title.isBlank()) {
                    notifications.add(title + ": " + notification.getMessage());
                } else {
                    notifications.add(notification.getMessage());
                }
            });
        }

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

        registrationRequests.stream()
                .filter(req -> req.getStatus() == DormRegistrationStatus.PENDING || req.getStatus() == DormRegistrationStatus.NEEDS_UPDATE)
                .findFirst()
                .ifPresent(req -> notifications.add("Yêu cầu đăng ký ký túc xá của bạn đang chờ duyệt."));

        registrationRequests.stream()
                .filter(req -> req.getStatus() == DormRegistrationStatus.APPROVED)
                .max(Comparator.comparing(this::lastUpdated))
                .ifPresent(req -> notifications.add("Yêu cầu đăng ký ký túc xá đã được chấp nhận."));

        registrationRequests.stream()
                .filter(req -> req.getStatus() == DormRegistrationStatus.REJECTED)
                .max(Comparator.comparing(this::lastUpdated))
                .ifPresent(req -> notifications.add("Yêu cầu đăng ký ký túc xá của bạn đã bị từ chối." +
                        (req.getAdminNotes() != null && !req.getAdminNotes().isBlank() ? " Lý do: " + req.getAdminNotes() : "")));

        if (activePeriod != null) {
            boolean alreadySubmitted = registrationRequests.stream()
                    .anyMatch(req -> req.getPeriod() != null && req.getPeriod().getId().equals(activePeriod.getId()));
            if (!alreadySubmitted) {
                String endTimeText = activePeriod.getEndTime() != null
                        ? DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(activePeriod.getEndTime())
                        : "(chưa xác định thời gian kết thúc)";
                notifications.add("Đợt đăng ký '" + activePeriod.getName() + "' đang mở tới " + endTimeText + ".");
            } else {
                notifications.add("Bạn đã gửi đăng ký trong đợt '" + activePeriod.getName() + "'. Theo dõi trạng thái tại mục Đăng ký KTX.");
            }
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

    private LocalDateTime lastUpdated(DormRegistrationRequest request) {
        return request.getUpdatedAt() != null ? request.getUpdatedAt() : request.getCreatedAt();
    }
}

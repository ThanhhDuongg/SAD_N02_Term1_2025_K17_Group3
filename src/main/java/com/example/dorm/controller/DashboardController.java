package com.example.dorm.controller;

import com.example.dorm.service.ContractService;
import com.example.dorm.service.FeeService;
import com.example.dorm.service.MaintenanceRequestService;
import com.example.dorm.service.RoomService;
import com.example.dorm.service.StudentService;
import com.example.dorm.service.ViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private ContractService contractService;
    @Autowired
    private FeeService feeService;
    @Autowired
    private MaintenanceRequestService maintenanceRequestService;
    @Autowired
    private ViolationService violationService;

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_STUDENT"));
        if (isStudent) {
            return "redirect:/student/dashboard";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        long studentCount = studentService.countStudents();
        long roomCount = roomService.countRooms();
        long contractCount = contractService.countContracts();
        long feeCount = feeService.countFees();
        long totalRequests = maintenanceRequestService.countAllRequests();
        long pendingRequests = maintenanceRequestService.countRequestsByStatus("PENDING");
        long inProgressRequests = maintenanceRequestService.countRequestsByStatus("IN_PROGRESS");
        long totalViolations = violationService.countViolations();
        long highSeverityViolations = violationService.countViolationsBySeverity("HIGH");

        model.addAttribute("studentCount", studentCount);
        model.addAttribute("roomCount", roomCount);
        model.addAttribute("contractCount", contractCount);
        model.addAttribute("feeCount", feeCount);
        model.addAttribute("maintenanceTotal", totalRequests);
        model.addAttribute("maintenancePending", pendingRequests);
        model.addAttribute("maintenanceInProgress", inProgressRequests);
        model.addAttribute("violationTotal", totalViolations);
        model.addAttribute("violationHigh", highSeverityViolations);

        // Backwards compatibility for templates expecting pluralized attribute names
        model.addAttribute("studentsCount", studentCount);
        model.addAttribute("roomsCount", roomCount);
        model.addAttribute("contractsCount", contractCount);
        model.addAttribute("feesCount", feeCount);
        return "dashboard";
    }
}

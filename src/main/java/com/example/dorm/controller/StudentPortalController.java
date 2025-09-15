package com.example.dorm.controller;

import com.example.dorm.model.Student;
import com.example.dorm.model.Contract;
import com.example.dorm.model.Fee;
import com.example.dorm.model.MaintenanceRequest;
import com.example.dorm.service.StudentService;
import com.example.dorm.service.ContractService;
import com.example.dorm.service.FeeService;
import com.example.dorm.service.MaintenanceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentPortalController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private FeeService feeService;

    @Autowired
    private MaintenanceRequestService maintenanceRequestService;

    @GetMapping("/dashboard")
    public String studentDashboard(Model model, Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<Student> studentOpt = studentService.findByUsername(username);

            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                model.addAttribute("student", student);

                // Get current contract
                Contract currentContract = contractService.findLatestContractByStudentId(student.getId());
                model.addAttribute("currentContract", currentContract);

                // Get unpaid fees
                var unpaidFees = feeService.getUnpaidFeesByStudent(student.getId());
                model.addAttribute("unpaidFees", unpaidFees);
                model.addAttribute("unpaidCount", unpaidFees.size());

                // Get maintenance requests
                var maintenanceRequests = maintenanceRequestService.getRequestsByStudent(student.getId());
                model.addAttribute("maintenanceRequests", maintenanceRequests);

                return "student/dashboard";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy thông tin sinh viên");
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tải dashboard: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<Student> studentOpt = studentService.findByUsername(username);

            if (studentOpt.isPresent()) {
                model.addAttribute("student", studentOpt.get());
                return "student/profile";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy thông tin sinh viên");
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tải profile: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/contracts")
    public String viewContracts(Model model, Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<Student> studentOpt = studentService.findByUsername(username);

            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                var contracts = contractService.getContractsByStudent(student.getId());
                model.addAttribute("contracts", contracts);
                model.addAttribute("student", student);
                return "student/contracts";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy thông tin sinh viên");
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tải hợp đồng: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/fees")
    public String viewFees(Model model, Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<Student> studentOpt = studentService.findByUsername(username);

            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                var fees = feeService.getFeesByStudent(student.getId());
                model.addAttribute("fees", fees);
                model.addAttribute("student", student);
                return "student/fees";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy thông tin sinh viên");
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tải phí: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/maintenance/new")
    public String newMaintenanceRequest(Model model, Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<Student> studentOpt = studentService.findByUsername(username);

            if (studentOpt.isPresent()) {
                model.addAttribute("maintenanceRequest", new MaintenanceRequest());
                model.addAttribute("student", studentOpt.get());
                return "student/maintenance-form";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy thông tin sinh viên");
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tải form: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/maintenance")
    public String createMaintenanceRequest(@ModelAttribute MaintenanceRequest request,
                                           Authentication authentication,
                                           RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            Optional<Student> studentOpt = studentService.findByUsername(username);

            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                request.setStudent(student);
                request.setRoom(student.getRoom());
                request.setStatus("PENDING");

                maintenanceRequestService.createRequest(request);

                redirectAttributes.addFlashAttribute("message", "Yêu cầu sửa chữa đã được gửi thành công!");
                redirectAttributes.addFlashAttribute("alertClass", "alert-success");
                return "redirect:/student/dashboard";
            } else {
                redirectAttributes.addFlashAttribute("message", "Không tìm thấy thông tin sinh viên");
                redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
                return "redirect:/student/dashboard";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Lỗi khi tạo yêu cầu: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/student/dashboard";
        }
    }
}
package com.example.dorm.controller;

import com.example.dorm.model.DormRegistrationRequest;
import com.example.dorm.model.DormRegistrationStatus;
import com.example.dorm.service.DormRegistrationRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/registrations")
public class DormRegistrationRequestController {

    private final DormRegistrationRequestService dormRegistrationRequestService;

    public DormRegistrationRequestController(DormRegistrationRequestService dormRegistrationRequestService) {
        this.dormRegistrationRequestService = dormRegistrationRequestService;
    }

    @ModelAttribute("statusOptions")
    public DormRegistrationStatus[] statusOptions() {
        return DormRegistrationStatus.values();
    }

    @GetMapping
    public String list(@RequestParam(value = "status", required = false) String status,
                       @RequestParam(value = "query", required = false) String keyword,
                       Model model) {
        List<DormRegistrationRequest> requests = dormRegistrationRequestService.findAll(status, keyword);
        model.addAttribute("requests", requests);
        model.addAttribute("selectedStatus", status != null ? status.toUpperCase() : "");
        model.addAttribute("searchQuery", keyword != null ? keyword : "");
        return "registrations/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return dormRegistrationRequestService.findById(id)
                .map(request -> {
                    model.addAttribute("request", request);
                    return "registrations/detail";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("message", "Không tìm thấy yêu cầu đăng ký");
                    redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
                    return "redirect:/registrations";
                });
    }

    @PostMapping("/{id}/update")
    public String updateRequest(@PathVariable Long id,
                                @ModelAttribute("request") DormRegistrationRequest form,
                                RedirectAttributes redirectAttributes) {
        try {
            dormRegistrationRequestService.updateRequest(id, form);
            redirectAttributes.addFlashAttribute("message", "Đã cập nhật thông tin yêu cầu");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Cập nhật thất bại: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/registrations/" + id;
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam("status") DormRegistrationStatus status,
                               @RequestParam(value = "adminNotes", required = false) String adminNotes,
                               RedirectAttributes redirectAttributes) {
        try {
            DormRegistrationRequest updated = dormRegistrationRequestService.updateStatus(id, status, adminNotes);
            redirectAttributes.addFlashAttribute("message", switch (updated.getStatus()) {
                case APPROVED -> "Đã phê duyệt yêu cầu đăng ký.";
                case REJECTED -> "Đã từ chối yêu cầu đăng ký.";
                case NEEDS_UPDATE -> "Đã yêu cầu sinh viên bổ sung thông tin.";
                default -> "Đã cập nhật trạng thái yêu cầu.";
            });
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Không thể cập nhật trạng thái: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/registrations/" + id;
    }
}

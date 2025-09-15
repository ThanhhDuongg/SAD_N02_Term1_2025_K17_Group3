package com.example.dorm.controller;

import com.example.dorm.model.MaintenanceRequest;
import com.example.dorm.service.MaintenanceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/maintenance")
public class MaintenanceRequestController {

    private static final List<String> STATUS_OPTIONS = List.of("PENDING", "IN_PROGRESS", "COMPLETED", "REJECTED");

    @Autowired
    private MaintenanceRequestService maintenanceRequestService;

    @ModelAttribute("statusOptions")
    public List<String> statusOptions() {
        return STATUS_OPTIONS;
    }

    @GetMapping
    public String listRequests(@RequestParam(value = "status", required = false) String status,
                               Model model) {
        List<MaintenanceRequest> requests = maintenanceRequestService.getRequestsByStatus(status);
        List<MaintenanceRequest> allRequests = maintenanceRequestService.getAllRequests();

        Map<String, Long> statusSummary = allRequests.stream()
                .collect(Collectors.groupingBy(MaintenanceRequest::getStatus, Collectors.counting()));

        model.addAttribute("requests", requests);
        model.addAttribute("selectedStatus", status != null ? status.toUpperCase() : "");
        model.addAttribute("statusSummary", statusSummary);
        return "maintenance/list";
    }

    @GetMapping("/{id}")
    public String viewRequest(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return maintenanceRequestService.getRequest(id)
                .map(request -> {
                    model.addAttribute("request", request);
                    return "maintenance/detail";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("message", "Không tìm thấy yêu cầu");
                    redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
                    return "redirect:/maintenance";
                });
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam("status") String status,
                               RedirectAttributes redirectAttributes) {
        try {
            maintenanceRequestService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("message", "Cập nhật trạng thái thành công");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Cập nhật trạng thái thất bại: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/maintenance/" + id;
    }
}

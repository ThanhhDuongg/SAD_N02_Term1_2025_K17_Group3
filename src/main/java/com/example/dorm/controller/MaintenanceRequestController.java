package com.example.dorm.controller;

import com.example.dorm.model.MaintenanceRequest;
import com.example.dorm.service.MaintenanceRequestService;
import com.example.dorm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
    private static final List<String> REQUEST_TYPES = List.of("MAINTENANCE", "INCIDENT", "ROOM_TRANSFER");

    @Autowired
    private MaintenanceRequestService maintenanceRequestService;

    @Autowired
    private UserService userService;

    @ModelAttribute("statusOptions")
    public List<String> statusOptions() {
        return STATUS_OPTIONS;
    }

    @ModelAttribute("requestTypes")
    public List<String> requestTypes() {
        return REQUEST_TYPES;
    }

    @GetMapping
    public String listRequests(@RequestParam(value = "status", required = false) String status,
                               @RequestParam(value = "type", required = false) String type,
                               @RequestParam(value = "query", required = false) String keyword,
                               @RequestParam(value = "mine", required = false) Boolean mine,
                               Authentication authentication,
                               Model model) {
        String assignedUsername = null;
        if (Boolean.TRUE.equals(mine) && authentication != null) {
            assignedUsername = authentication.getName();
        }

        List<MaintenanceRequest> requests = maintenanceRequestService.getRequests(status, type, keyword, assignedUsername);
        List<MaintenanceRequest> allRequests = maintenanceRequestService.getAllRequests();

        Map<String, Long> statusSummary = allRequests.stream()
                .collect(Collectors.groupingBy(MaintenanceRequest::getStatus, Collectors.counting()));

        model.addAttribute("requests", requests);
        model.addAttribute("selectedStatus", status != null ? status.toUpperCase() : "");
        model.addAttribute("selectedType", type != null ? type.toUpperCase() : "");
        model.addAttribute("searchQuery", keyword != null ? keyword : "");
        model.addAttribute("assignedToMe", Boolean.TRUE.equals(mine));
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
                               @RequestParam(value = "resolutionNotes", required = false) String resolutionNotes,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            com.example.dorm.model.User handledBy = null;
            if (authentication != null) {
                handledBy = userService.findByUsername(authentication.getName()).orElse(null);
            }
            maintenanceRequestService.updateStatus(id, status, resolutionNotes, handledBy);
            redirectAttributes.addFlashAttribute("message", "Cập nhật trạng thái thành công");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Cập nhật trạng thái thất bại: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/maintenance/" + id;
    }
}

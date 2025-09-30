package com.example.dorm.controller;

import com.example.dorm.model.MaintenanceRequest;
import com.example.dorm.model.RoleName;
import com.example.dorm.model.User;
import com.example.dorm.service.MaintenanceRequestService;
import com.example.dorm.service.UserService;
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

    private final MaintenanceRequestService maintenanceRequestService;
    private final UserService userService;

    public MaintenanceRequestController(MaintenanceRequestService maintenanceRequestService,
                                        UserService userService) {
        this.maintenanceRequestService = maintenanceRequestService;
        this.userService = userService;
    }

    @ModelAttribute("statusOptions")
    public List<String> statusOptions() {
        return STATUS_OPTIONS;
    }

    @ModelAttribute("requestTypes")
    public List<String> requestTypes() {
        return REQUEST_TYPES;
    }

    @ModelAttribute("staffMembers")
    public List<User> staffMembers() {
        return userService.findUsersByRole(RoleName.ROLE_STAFF);
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
                               RedirectAttributes redirectAttributes) {
        try {
            maintenanceRequestService.updateStatus(id, status, resolutionNotes, null);
            redirectAttributes.addFlashAttribute("message", "Cập nhật trạng thái thành công");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Cập nhật trạng thái thất bại: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/maintenance/" + id;
    }

    @PostMapping("/{id}/assign")
    public String assignRequest(@PathVariable Long id,
                                @RequestParam("assigneeId") Long assigneeId,
                                RedirectAttributes redirectAttributes) {
        try {
            User assignee = userService.findById(assigneeId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));
            if (!userService.hasRole(assignee, RoleName.ROLE_STAFF)) {
                throw new IllegalArgumentException("Người được chọn không phải là nhân viên hỗ trợ");
            }
            maintenanceRequestService.assignHandler(id, assignee);
            redirectAttributes.addFlashAttribute("message", "Đã phân công yêu cầu cho " + assignee.getUsername());
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Phân công thất bại: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/maintenance/" + id;
    }

    @PostMapping("/{id}/accept")
    public String acceptRequest(@PathVariable Long id,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            if (authentication == null) {
                throw new IllegalStateException("Bạn cần đăng nhập để tiếp nhận yêu cầu");
            }
            User current = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new IllegalStateException("Không tìm thấy tài khoản"));
            if (!userService.hasRole(current, RoleName.ROLE_STAFF)) {
                throw new IllegalStateException("Chỉ nhân viên hỗ trợ mới có thể tiếp nhận yêu cầu");
            }
            MaintenanceRequest request = maintenanceRequestService.getRequiredRequest(id);
            User existing = request.getHandledBy();
            if (existing != null && !existing.getId().equals(current.getId())) {
                throw new IllegalStateException("Yêu cầu đã được phân công cho " + existing.getUsername());
            }
            maintenanceRequestService.assignHandler(id, current);
            redirectAttributes.addFlashAttribute("message", "Bạn đã tiếp nhận yêu cầu này");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Không thể tiếp nhận yêu cầu: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/maintenance/" + id;
    }

    @PostMapping("/{id}/unassign")
    public String unassignRequest(@PathVariable Long id,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            MaintenanceRequest request = maintenanceRequestService.getRequiredRequest(id);
            boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
            boolean isCurrentHandler = authentication != null
                    && request.getHandledBy() != null
                    && request.getHandledBy().getUsername().equals(authentication.getName());
            if (!isAdmin && !isCurrentHandler) {
                throw new IllegalStateException("Bạn không có quyền hủy phân công yêu cầu này");
            }
            maintenanceRequestService.unassignHandler(id);
            redirectAttributes.addFlashAttribute("message", "Đã hủy phân công yêu cầu");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Không thể hủy phân công: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/maintenance/" + id;
    }
}

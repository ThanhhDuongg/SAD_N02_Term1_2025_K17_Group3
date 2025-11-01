package com.example.dorm.controller;

import com.example.dorm.model.RoomType;
import com.example.dorm.model.RoomTypePriceHistory;
import com.example.dorm.service.RoomTypeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/room-types")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    public RoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    @GetMapping
    public String list(Model model) {
        List<RoomType> roomTypes = roomTypeService.getAllRoomTypes();
        Map<Long, Long> roomCounts = roomTypeService.getRoomCounts();
        Map<Long, Long> activeContracts = new HashMap<>();
        for (RoomType roomType : roomTypes) {
            activeContracts.put(roomType.getId(), roomTypeService.countActiveContracts(roomType.getId()));
        }
        model.addAttribute("roomTypes", roomTypes);
        model.addAttribute("roomCounts", roomCounts);
        model.addAttribute("activeContracts", activeContracts);
        return "room_types/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("roomType", new RoomType());
        return "room_types/form";
    }

    @PostMapping
    public String create(@ModelAttribute RoomType roomType,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        try {
            roomTypeService.createRoomType(roomType);
            redirectAttributes.addFlashAttribute("message", "Thêm loại phòng thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/room-types";
        } catch (Exception e) {
            model.addAttribute("roomType", roomType);
            model.addAttribute("formError", e.getMessage());
            return "room_types/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        RoomType roomType = roomTypeService.getRoomType(id);
        model.addAttribute("roomType", roomType);
        return "room_types/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute RoomType roomType,
                         @RequestParam(value = "priceNote", required = false) String priceNote,
                         @RequestParam(value = "priceEffectiveDate", required = false)
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate priceEffectiveDate,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        try {
            RoomTypeService.RoomTypeUpdateResult result = roomTypeService.updateRoomType(id, roomType, priceNote, priceEffectiveDate);
            redirectAttributes.addFlashAttribute("message", "Cập nhật loại phòng thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            if (result.priceChanged()) {
                if (result.affectedActiveContracts() > 0) {
                    redirectAttributes.addFlashAttribute("secondaryMessage",
                            "Cảnh báo: Có " + result.affectedActiveContracts() + " hợp đồng đang hiệu lực chịu ảnh hưởng bởi thay đổi giá.");
                    redirectAttributes.addFlashAttribute("secondaryAlertClass", "alert-warning");
                } else {
                    redirectAttributes.addFlashAttribute("secondaryMessage",
                            "Giá thuê đã thay đổi. Hệ thống đã ghi nhận lịch sử giá cho loại phòng này.");
                    redirectAttributes.addFlashAttribute("secondaryAlertClass", "alert-info");
                }
            }
            return "redirect:/room-types";
        } catch (Exception e) {
            roomType.setId(id);
            model.addAttribute("roomType", roomType);
            model.addAttribute("priceNote", priceNote);
            model.addAttribute("priceEffectiveDate", priceEffectiveDate);
            model.addAttribute("formError", e.getMessage());
            return "room_types/form";
        }
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roomTypeService.deleteRoomType(id);
            redirectAttributes.addFlashAttribute("message", "Xóa loại phòng thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Xóa loại phòng thất bại: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/room-types";
    }

    @GetMapping("/{id}/history")
    public String priceHistory(@PathVariable Long id, Model model) {
        RoomType roomType = roomTypeService.getRoomType(id);
        List<RoomTypePriceHistory> historyEntries = roomTypeService.getPriceHistory(id);
        model.addAttribute("roomType", roomType);
        model.addAttribute("historyEntries", historyEntries);
        model.addAttribute("activeContracts", roomTypeService.countActiveContracts(id));
        return "room_types/history";
    }
}

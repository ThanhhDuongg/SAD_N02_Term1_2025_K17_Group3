package com.example.dorm.controller;

import com.example.dorm.model.Room;
import com.example.dorm.service.BuildingService;
import com.example.dorm.service.RoomService;
import com.example.dorm.util.PageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;
    private final BuildingService buildingService;

    public RoomController(RoomService roomService, BuildingService buildingService) {
        this.roomService = roomService;
        this.buildingService = buildingService;
    }

    @ModelAttribute("buildings")
    public List<com.example.dorm.model.Building> buildings() {
        return buildingService.getAllBuildings();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Room room = roomService.getRoomWithStudents(id);
        model.addAttribute("room", room);
        model.addAttribute("occupancy", room.getStudents().size());
        return "rooms/detail";
    }

    @GetMapping
    public String listRooms(@RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "buildingId", required = false) Long buildingId,
                            @RequestParam(name = "page", defaultValue = "0") int page,
                            @RequestParam(name = "size", defaultValue = "10") int size,
                            Model model) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Room> roomsPage = roomService.searchRooms(search, buildingId, pageable);
        model.addAttribute("roomsPage", roomsPage);

        Map<Long, Long> occupancies = new LinkedHashMap<>();
        roomsPage.getContent().forEach(room ->
                occupancies.put(room.getId(), roomService.getCurrentOccupancy(room.getId())));
        model.addAttribute("occupancies", occupancies);
        model.addAttribute("pageNumbers", PageUtils.buildPageNumbers(roomsPage));
        model.addAttribute("search", search);
        model.addAttribute("selectedBuildingId", buildingId);
        return "rooms/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("room", new Room());
        model.addAttribute("selectedBuildingId", null);
        return "rooms/form";
    }

    @PostMapping
    public String createRoom(@ModelAttribute Room room,
                             @RequestParam("buildingId") Long buildingId,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        try {
            roomService.createRoom(room, buildingId);
            redirectAttributes.addFlashAttribute("message", "Thêm phòng thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/rooms";
        } catch (Exception e) {
            model.addAttribute("room", room);
            model.addAttribute("selectedBuildingId", buildingId);
            model.addAttribute("formError", e.getMessage());
            return "rooms/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Room room = roomService.getRoom(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng với ID: " + id));
        model.addAttribute("room", room);
        model.addAttribute("selectedBuildingId", room.getBuilding() != null ? room.getBuilding().getId() : null);
        return "rooms/form";
    }

    @PostMapping("/{id}")
    public String updateRoom(@PathVariable("id") Long id,
                             @ModelAttribute Room room,
                             @RequestParam("buildingId") Long buildingId,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        try {
            roomService.getRoom(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng với ID: " + id));
            roomService.updateRoom(id, room, buildingId);
            redirectAttributes.addFlashAttribute("message", "Cập nhật phòng thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/rooms";
        } catch (Exception e) {
            room.setId(id);
            model.addAttribute("room", room);
            model.addAttribute("selectedBuildingId", buildingId);
            model.addAttribute("formError", e.getMessage());
            return "rooms/form";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteRoom(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            roomService.getRoom(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng với ID: " + id));
            roomService.deleteRoom(id);
            redirectAttributes.addFlashAttribute("message", "Xoá phòng thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Xoá phòng thất bại: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/rooms";
    }
}

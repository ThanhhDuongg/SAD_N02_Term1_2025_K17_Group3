package com.example.dorm.controller;

import com.example.dorm.model.Room;
import com.example.dorm.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @GetMapping
    public String listRooms(@RequestParam(value = "search", required = false) String search,
                            @RequestParam(name = "page", defaultValue = "0") int page,
                            @RequestParam(name = "size", defaultValue = "10") int size,
                            Model model) {
        try {
            var pageable = org.springframework.data.domain.PageRequest.of(page, size);
            var roomsPage = roomService.searchRooms(search, pageable);
            model.addAttribute("roomsPage", roomsPage);
             java.util.Map<Long, Long> occupancies = new java.util.HashMap<>();
            for (Room r : roomsPage.getContent()) {
                occupancies.put(r.getId(), roomService.getCurrentOccupancy(r.getId()));
            }
            model.addAttribute("occupancies", occupancies);
            int totalPages = roomsPage.getTotalPages();
            if (totalPages > 0) {
                java.util.List<Integer> pageNumbers =
                        java.util.stream.IntStream.rangeClosed(1, totalPages)
                                .boxed().toList();
                model.addAttribute("pageNumbers", pageNumbers);
            }
            model.addAttribute("search", search);
            return "rooms/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tải danh sách phòng: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        try {
            model.addAttribute("room", new Room());
            return "rooms/form";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi hiển thị form tạo phòng: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping
    public String createRoom(@ModelAttribute Room room, RedirectAttributes redirectAttributes, Model model) {
        try {
            roomService.createRoom(room);
            redirectAttributes.addFlashAttribute("message", "Thêm phòng thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/rooms";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Thêm phòng thất bại!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/rooms";
        }
    }

    @GetMapping("/{id}")
    public String viewRoom(@PathVariable("id") Long id, Model model) {
        try {
            Optional<Room> roomOptional = roomService.getRoom(id);
            if (roomOptional.isPresent()) {
                model.addAttribute("room", roomOptional.get());
                long occupancy = roomService.getCurrentOccupancy(id);
                model.addAttribute("occupancy", occupancy);
                return "rooms/detail";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy phòng với ID: " + id);
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi xem phòng: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}/edit")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        try {
            Optional<Room> roomOptional = roomService.getRoom(id);
            if (roomOptional.isPresent()) {
                model.addAttribute("room", roomOptional.get());
                return "rooms/form";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy phòng với ID: " + id);
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi hiển thị form sửa phòng: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/{id}")
    public String updateRoom(@PathVariable("id") Long id, @ModelAttribute Room room, RedirectAttributes redirectAttributes, Model model) {
        try {
            Optional<Room> roomOptional = roomService.getRoom(id);
            if (roomOptional.isPresent()) {
                roomService.updateRoom(id, room);
                redirectAttributes.addFlashAttribute("message", "Cập nhật phòng thành công!");
                redirectAttributes.addFlashAttribute("alertClass", "alert-success");
                return "redirect:/rooms";
            } else {
                redirectAttributes.addFlashAttribute("message", "Không tìm thấy phòng với ID: " + id);
                redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
                return "redirect:/rooms";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Cập nhật phòng thất bại!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/rooms";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteRoom(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Model model) {
        try {
            Optional<Room> roomOptional = roomService.getRoom(id);
            if (roomOptional.isPresent()) {
                roomService.deleteRoom(id);
                redirectAttributes.addFlashAttribute("message", "Xoá phòng thành công!");
                redirectAttributes.addFlashAttribute("alertClass", "alert-success");
                return "redirect:/rooms";
            } else {
                redirectAttributes.addFlashAttribute("message", "Không tìm thấy phòng với ID: " + id);
                redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
                return "redirect:/rooms";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Xoá phòng thất bại!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/rooms";
        }
    }

    // search handled by listRooms
}

package Controller;

import Model.Room;
import Repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/rooms")
public class RoomController {
    @Autowired
    private RoomRepository roomRepository;

    @GetMapping
    public String listRooms(Model model) {
        try {
            model.addAttribute("rooms", roomRepository.findAll());
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
    public String createRoom(@ModelAttribute Room room, Model model) {
        try {
            if ("Phòng bốn".equals(room.getType())) {
                room.setPrice(2000000);
            } else if ("Phòng tám".equals(room.getType())) {
                room.setPrice(1200000);
            }
            roomRepository.save(room);
            return "redirect:/rooms";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tạo phòng: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}")
    public String viewRoom(@PathVariable Long id, Model model) {
        try {
            Optional<Room> roomOptional = roomRepository.findById(id);
            if (roomOptional.isPresent()) {
                model.addAttribute("room", roomOptional.get());
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
    public String showUpdateForm(@PathVariable Long id, Model model) {
        try {
            Optional<Room> roomOptional = roomRepository.findById(id);
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
    public String updateRoom(@PathVariable Long id, @ModelAttribute Room room, Model model) {
        try {
            Optional<Room> roomOptional = roomRepository.findById(id);
            if (roomOptional.isPresent()) {
                Room existingRoom = roomOptional.get();
                room.setId(id);
                room.setType(existingRoom.getType()); // Không cho sửa loại phòng
                room.setPrice(existingRoom.getPrice()); // Không cho sửa giá
                roomRepository.save(room);
                return "redirect:/rooms";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy phòng với ID: " + id);
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi cập nhật phòng: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteRoom(@PathVariable Long id, Model model) {
        try {
            Optional<Room> roomOptional = roomRepository.findById(id);
            if (roomOptional.isPresent()) {
                roomRepository.deleteById(id);
                return "redirect:/rooms";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy phòng với ID: " + id);
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi xoá phòng: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/search")
    public String searchRooms(@RequestParam("search") String search, Model model) {
        try {
            if (search == null || search.trim().isEmpty()) {
                model.addAttribute("rooms", roomRepository.findAll());
            } else {
                model.addAttribute("rooms", roomRepository.findByNumberContainingIgnoreCaseOrTypeContainingIgnoreCase(search, search));
            }
            model.addAttribute("search", search);
            return "rooms/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tìm kiếm phòng: " + e.getMessage());
            return "error";
        }
    }
}

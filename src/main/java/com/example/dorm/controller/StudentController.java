package com.example.dorm.controller;

import com.example.dorm.model.Student;
import com.example.dorm.service.RoomService;
import com.example.dorm.service.StudentService;
import com.example.dorm.service.ContractService;
import com.example.dorm.model.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/students")
public class StudentController {
    private static final Logger log = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private ContractService contractService;

    @GetMapping
    public String listStudents(@RequestParam(value = "search", required = false) String search,
                               @RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "10") int size,
                               Model model) {
        var pageable = org.springframework.data.domain.PageRequest.of(page, size);
        var studentsPage = studentService.searchStudents(search, pageable);
        model.addAttribute("studentsPage", studentsPage);
        int totalPages = studentsPage.getTotalPages();
        java.util.List<Integer> pageNumbers = totalPages > 0
                ? java.util.stream.IntStream.rangeClosed(1, totalPages)
                .boxed().toList()
                : java.util.Collections.emptyList();
        model.addAttribute("pageNumbers", pageNumbers);
        model.addAttribute("search", search);
        return "students/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("rooms", roomService.getAllRooms());
        return "students/form";
    }

    @PostMapping
    public String createStudent(
            @Valid @ModelAttribute Student student,
            BindingResult bindingResult,
            @RequestParam(value = "contractStartDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate contractStartDate,
            @RequestParam(value = "contractEndDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate contractEndDate,
            @RequestParam(value = "contractStatus", required = false) String contractStatus,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("rooms", roomService.getAllRooms());
            return "students/form";
        }
        try {
            var saved = studentService.saveStudent(student);
            if (student.getRoom() != null && contractStartDate != null && contractEndDate != null) {
                Contract c = new Contract();
                c.setStudent(saved);
                c.setRoom(saved.getRoom());
                c.setStartDate(contractStartDate);
                c.setEndDate(contractEndDate);
                c.setStatus(contractStatus != null ? contractStatus : "ACTIVE");
                contractService.createContract(c);
            }
            redirectAttributes.addFlashAttribute("message", "Thêm sinh viên thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/students";
        } catch (Exception e) {
            log.error("Không thể tạo sinh viên", e);
            redirectAttributes.addFlashAttribute("message", "Thêm sinh viên thất bại: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/students";
        }
    }


    @GetMapping("/{id}")
    public String viewStudent(@PathVariable("id") Long id, Model model) {
        Student student = studentService.getStudent(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên với ID: " + id));
        model.addAttribute("student", student);
        return "students/detail";
    }

    @GetMapping("/{id}/edit")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Student student = studentService.getStudent(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên với ID: " + id));
        model.addAttribute("student", student);
        model.addAttribute("rooms", roomService.getAllRooms());
        Contract latestContract = contractService.findLatestContractByStudentId(id);
        if (latestContract != null) {
            model.addAttribute("contractStartDate", latestContract.getStartDate());
            model.addAttribute("contractEndDate", latestContract.getEndDate());
        }
        return "students/form";
    }

    @PostMapping("/{id}")
    public String updateStudent(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute Student student,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("rooms", roomService.getAllRooms());
            return "students/form";
        }
        try {
            studentService.getStudent(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên với ID: " + id));
            student.setId(id);
            studentService.saveStudent(student);
            redirectAttributes.addFlashAttribute("message", "Cập nhật sinh viên thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/students";
        } catch (Exception e) {
            log.error("Không thể cập nhật sinh viên với id {}", id, e);
            redirectAttributes.addFlashAttribute("message", "Cập nhật sinh viên thất bại: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/students";
        }
    }


    @GetMapping("/{id}/delete")
    public String deleteStudent(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Model model) {
        try {
            studentService.getStudent(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên với ID: " + id));
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("message", "Xoá sinh viên thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/students";
        } catch (Exception e) {
            log.error("Không thể xoá sinh viên với id {}", id, e);
            redirectAttributes.addFlashAttribute("message", "Xoá sinh viên thất bại: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/students";
        }
    }

    @GetMapping(value = "/search", produces = "application/json")
    @ResponseBody
    public java.util.List<java.util.Map<String, Object>> autocomplete(@RequestParam("term") String term) {
        var pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        var studentsPage = studentService.searchStudents(term, pageable);
        return studentsPage.getContent().stream().map(s -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", s.getId());
            map.put("label", s.getCode() + " - " + s.getName());
            return map;
        }).toList();
    }

    // search handled by listStudents
}
package com.example.dorm.controller;

import com.example.dorm.model.Contract;
import com.example.dorm.model.Student;
import com.example.dorm.service.ContractService;
import com.example.dorm.service.RoomService;
import com.example.dorm.service.StudentService;
import com.example.dorm.util.PageUtils;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/students")
public class StudentController {
    private static final Logger log = LoggerFactory.getLogger(StudentController.class);

    private final StudentService studentService;
    private final RoomService roomService;
    private final ContractService contractService;

    public StudentController(StudentService studentService,
                             RoomService roomService,
                             ContractService contractService) {
        this.studentService = studentService;
        this.roomService = roomService;
        this.contractService = contractService;
    }

    @GetMapping
    public String listStudents(@RequestParam(value = "search", required = false) String search,
                               @RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "10") int size,
                               Model model) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Student> studentsPage = studentService.searchStudents(search, pageable);
        model.addAttribute("studentsPage", studentsPage);
        model.addAttribute("pageNumbers", PageUtils.buildPageNumbers(studentsPage));
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
            Student saved = studentService.saveStudent(student);
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
    public List<Map<String, Object>> autocomplete(@RequestParam("term") String term) {
        Page<Student> studentsPage = studentService.searchStudents(term, PageRequest.of(0, 10));
        return studentsPage.getContent().stream()
                .map(student -> Map.<String, Object>of(
                        "id", student.getId(),
                        "label", student.getCode() + " - " + student.getName()))
                .toList();
    }
}
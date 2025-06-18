package com.example.dorm.controller;

import com.example.dorm.model.Student;
import com.example.dorm.repository.RoomRepository;
import com.example.dorm.repository.StudentRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/students")
public class StudentController {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RoomRepository roomRepository;

    @GetMapping
    public String listStudents(Model model) {
        try {
            model.addAttribute("students", studentRepository.findAll());
            return "students/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tải danh sách sinh viên: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        try {
            model.addAttribute("student", new Student());
            model.addAttribute("rooms", roomRepository.findAll());
            return "students/form";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi hiển thị form tạo sinh viên: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping
    public String createStudent(@ModelAttribute Student student, Model model) {
        try {
            studentRepository.save(student);
            return "redirect:/students";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tạo sinh viên: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        try {
            Optional<Student> studentOptional = studentRepository.findById(id);
            if (studentOptional.isPresent()) {
                model.addAttribute("student", studentOptional.get());
                return "students/detail";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy sinh viên với ID: " + id);
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi xem thông tin sinh viên: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}/edit")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        try {
            Optional<Student> studentOptional = studentRepository.findById(id);
            if (studentOptional.isPresent()) {
                model.addAttribute("student", studentOptional.get());
                model.addAttribute("rooms", roomRepository.findAll());
                return "students/form";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy sinh viên với ID: " + id);
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi hiển thị form cập nhật: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/{id}")
    public String updateStudent(@PathVariable Long id, @ModelAttribute Student student, Model model) {
        try {
            Optional<Student> studentOptional = studentRepository.findById(id);
            if (studentOptional.isPresent()) {
                student.setId(id);
                studentRepository.save(student);
                return "redirect:/students";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy sinh viên với ID: " + id);
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi cập nhật sinh viên: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteStudent(@PathVariable Long id, Model model) {
        try {
            Optional<Student> studentOptional = studentRepository.findById(id);
            if (studentOptional.isPresent()) {
                studentRepository.deleteById(id);
                return "redirect:/students";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy sinh viên với ID: " + id);
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi xoá sinh viên: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/search")
    public String searchStudents(@RequestParam("search") String search, Model model) {
        try {
            if (search == null || search.trim().isEmpty()) {
                model.addAttribute("students", studentRepository.findAll());
            } else {
                model.addAttribute("students", studentRepository.findByNameContainingIgnoreCase(search));
            }
            model.addAttribute("search", search);
            return "students/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tìm kiếm sinh viên: " + e.getMessage());
            return "error";
        }
    }
}
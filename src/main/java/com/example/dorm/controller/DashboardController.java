package com.example.dorm.controller;

import com.example.dorm.service.StudentService;
import com.example.dorm.service.RoomService;
import com.example.dorm.service.ContractService;
import com.example.dorm.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class DashboardController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private ContractService contractService;
    @Autowired
    private FeeService feeService;

    @GetMapping("/")
    public String showDashboard(Model model) {
        model.addAttribute("studentCount", studentService.getAllStudents(org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        model.addAttribute("roomCount", roomService.getAllRooms(org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        model.addAttribute("contractCount", contractService.getAllContracts(org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        model.addAttribute("feeCount", feeService.getAllFees(org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        return "dashboard";
    }
}
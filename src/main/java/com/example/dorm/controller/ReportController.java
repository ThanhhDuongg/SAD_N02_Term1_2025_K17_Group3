package com.example.dorm.controller;

import com.example.dorm.dto.BuildingSummary;
import com.example.dorm.model.Fee;
import com.example.dorm.model.MaintenanceRequest;
import com.example.dorm.model.PaymentStatus;
import com.example.dorm.service.BuildingService;
import com.example.dorm.service.FeeService;
import com.example.dorm.service.MaintenanceRequestService;
import com.example.dorm.service.RoomService;
import com.example.dorm.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final BuildingService buildingService;
    private final RoomService roomService;
    private final StudentService studentService;
    private final MaintenanceRequestService maintenanceRequestService;
    private final FeeService feeService;

    public ReportController(BuildingService buildingService,
                            RoomService roomService,
                            StudentService studentService,
                            MaintenanceRequestService maintenanceRequestService,
                            FeeService feeService) {
        this.buildingService = buildingService;
        this.roomService = roomService;
        this.studentService = studentService;
        this.maintenanceRequestService = maintenanceRequestService;
        this.feeService = feeService;
    }

    @GetMapping
    public String overview(Model model) {
        List<BuildingSummary> buildingSummaries = buildingService.getBuildingSummaries();
        buildingSummaries = buildingSummaries.stream()
                .sorted(Comparator.comparing(BuildingSummary::name, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        long totalCapacity = roomService.sumCapacity();
        long occupiedBeds = roomService.countOccupiedBeds();
        long availableBeds = Math.max(totalCapacity - occupiedBeds, 0);
        double occupancyRate = totalCapacity == 0 ? 0 : (double) occupiedBeds / totalCapacity * 100.0;

        List<MaintenanceRequest> allRequests = maintenanceRequestService.getAllRequests();
        Map<String, Long> maintenanceSummary = allRequests.stream()
                .collect(Collectors.groupingBy(MaintenanceRequest::getStatus, Collectors.counting()));

        List<Fee> fees = feeService.getAllFees();
        BigDecimal totalRevenue = fees.stream()
                .map(Fee::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal outstandingRevenue = fees.stream()
                .filter(fee -> fee.getPaymentStatus() == PaymentStatus.UNPAID)
                .map(Fee::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("buildingSummaries", buildingSummaries);
        model.addAttribute("buildingCount", buildingSummaries.size());
        model.addAttribute("studentCount", studentService.countStudents());
        model.addAttribute("totalCapacity", totalCapacity);
        model.addAttribute("occupiedBeds", occupiedBeds);
        model.addAttribute("availableBeds", availableBeds);
        model.addAttribute("occupancyRate", occupancyRate);
        model.addAttribute("maintenanceSummary", maintenanceSummary);
        model.addAttribute("totalRequests", allRequests.size());
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("outstandingRevenue", outstandingRevenue);

        return "reports/overview";
    }
}

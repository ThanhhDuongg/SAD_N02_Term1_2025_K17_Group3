package com.example.dorm.controller;

import com.example.dorm.dto.BuildingSummary;
import com.example.dorm.dto.FeeScopeSummary;
import com.example.dorm.dto.FeeTypeSummary;
import com.example.dorm.model.Fee;
import com.example.dorm.model.FeeScope;
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
                .filter(fee -> fee.getPaymentStatus() != PaymentStatus.PAID)
                .map(Fee::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<FeeTypeSummary> feeTypeSummaries = fees.stream()
                .filter(fee -> fee.getType() != null)
                .collect(Collectors.groupingBy(Fee::getType))
                .entrySet()
                .stream()
                .map(entry -> new FeeTypeSummary(
                        entry.getKey(),
                        entry.getValue().size(),
                        entry.getValue().stream()
                                .map(Fee::getAmount)
                                .filter(Objects::nonNull)
                                .reduce(BigDecimal.ZERO, BigDecimal::add),
                        entry.getValue().stream()
                                .filter(fee -> fee.getPaymentStatus() != PaymentStatus.PAID)
                                .count(),
                        entry.getValue().stream()
                                .filter(fee -> fee.getPaymentStatus() != PaymentStatus.PAID)
                                .map(Fee::getAmount)
                                .filter(Objects::nonNull)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)))
                .sorted(Comparator.comparing(summary -> summary.type().name()))
                .toList();

        List<FeeScopeSummary> feeScopeSummaries = fees.stream()
                .collect(Collectors.groupingBy(fee -> {
                    FeeScope scope = fee.getScope();
                    return scope != null ? scope : FeeScope.INDIVIDUAL;
                }))
                .entrySet()
                .stream()
                .map(entry -> new FeeScopeSummary(
                        entry.getKey(),
                        entry.getValue().size(),
                        entry.getValue().stream()
                                .map(Fee::getAmount)
                                .filter(Objects::nonNull)
                                .reduce(BigDecimal.ZERO, BigDecimal::add),
                        entry.getValue().stream()
                                .filter(fee -> fee.getPaymentStatus() != PaymentStatus.PAID)
                                .map(Fee::getAmount)
                                .filter(Objects::nonNull)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)))
                .sorted(Comparator.comparing(summary -> summary.scope().name()))
                .toList();

        List<Map<String, Object>> feeScopeChartData = feeScopeSummaries.stream()
                .map(summary -> Map.<String, Object>of(
                        "label", summary.displayName(),
                        "count", summary.feeCount(),
                        "totalAmount", summary.totalAmount(),
                        "unpaidAmount", summary.unpaidAmount()
                ))
                .toList();

        List<Map<String, Object>> feeTypeChartData = feeTypeSummaries.stream()
                .map(summary -> Map.<String, Object>of(
                        "label", summary.displayName(),
                        "count", summary.feeCount(),
                        "unpaidCount", summary.unpaidCount(),
                        "totalAmount", summary.totalAmount(),
                        "unpaidAmount", summary.unpaidAmount()
                ))
                .toList();

        List<Map<String, Object>> buildingOccupancyChartData = buildingSummaries.stream()
                .map(summary -> Map.<String, Object>of(
                        "label", summary.code() != null ? summary.code() : summary.name(),
                        "name", summary.name(),
                        "rate", summary.occupancyRate(),
                        "capacity", summary.capacity(),
                        "occupied", summary.occupiedBeds()
                ))
                .toList();

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
        model.addAttribute("feeTypeSummaries", feeTypeSummaries);
        model.addAttribute("feeScopeSummaries", feeScopeSummaries);
        model.addAttribute("feeScopeChartData", feeScopeChartData);
        model.addAttribute("feeTypeChartData", feeTypeChartData);
        model.addAttribute("buildingOccupancyChartData", buildingOccupancyChartData);

        return "reports/overview";
    }
}

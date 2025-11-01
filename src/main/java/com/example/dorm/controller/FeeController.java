package com.example.dorm.controller;

import com.example.dorm.model.Fee;
import com.example.dorm.model.FeeScope;
import com.example.dorm.service.ContractService;
import com.example.dorm.service.FeeService;
import com.example.dorm.util.PageUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/fees")
public class FeeController {

    private final FeeService feeService;
    private final ContractService contractService;

    public FeeController(FeeService feeService, ContractService contractService) {
        this.feeService = feeService;
        this.contractService = contractService;
    }

    @GetMapping
    public String listFees(@RequestParam(value = "search", required = false) String search,
                           @RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "size", defaultValue = "10") int size,
                           Model model) {
        feeService.refreshOverdueStatuses();
        PageRequest pageable = PageRequest.of(page, size);
        Page<Fee> feesPage = feeService.searchFees(search, pageable);
        model.addAttribute("feesPage", feesPage);
        model.addAttribute("pageNumbers", PageUtils.buildPageNumbers(feesPage));
        model.addAttribute("search", search);
        return "fees/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Fee fee = new Fee();
        fee.setScope(FeeScope.INDIVIDUAL);
        fee.setRoomId(null);
        model.addAttribute("fee", fee);
        model.addAttribute("contracts", contractService.getAllContracts());
        model.addAttribute("amountInputValue", "");
        return "fees/form";
    }

    @PostMapping
    public String createFee(@RequestParam("amountInput") String amountInput,
                            @Valid @ModelAttribute Fee fee,
                            BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("contracts", contractService.getAllContracts());
            model.addAttribute("amountInputValue", amountInput);
            return "fees/form";
        }
        try {
            BigDecimal totalAmount = parseAmount(amountInput);
            if (fee.getScope() == null) {
                fee.setScope(FeeScope.INDIVIDUAL);
            }
            feeService.createFee(fee, totalAmount);
            redirectAttributes.addFlashAttribute("message", "Thêm phí thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Thêm phí thất bại: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/fees";
    }

    @GetMapping("/{id}")
    public String viewFee(@PathVariable("id") Long id, Model model) {
        Fee fee = feeService.getFee(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phí với ID: " + id));
        model.addAttribute("fee", fee);
        return "fees/detail";
    }

    @GetMapping("/{id}/edit")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Fee fee = feeService.getFee(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phí với ID: " + id));
        if (fee.getScope() == null) {
            fee.setScope(FeeScope.INDIVIDUAL);
        }
        if (fee.getScope() == FeeScope.ROOM) {
            Long roomId = fee.getContract() != null && fee.getContract().getRoom() != null
                    ? fee.getContract().getRoom().getId()
                    : fee.getRoomId();
            fee.setRoomId(roomId);
        }
        model.addAttribute("fee", fee);
        model.addAttribute("contracts", contractService.getAllContracts());
        BigDecimal displayAmount = fee.getScope() == FeeScope.ROOM ? fee.getTotalAmount() : fee.getAmount();
        model.addAttribute("amountInputValue", formatAmount(displayAmount));
        return "fees/form";
    }

    @PostMapping("/{id}")
    public String updateFee(@PathVariable("id") Long id,
                            @RequestParam("amountInput") String amountInput,
                            @Valid @ModelAttribute Fee fee,
                            BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("contracts", contractService.getAllContracts());
            model.addAttribute("amountInputValue", amountInput);
            return "fees/form";
        }
        try {
            BigDecimal totalAmount = parseAmount(amountInput);
            if (fee.getScope() == null) {
                fee.setScope(FeeScope.INDIVIDUAL);
            }
            feeService.updateFee(id, fee, totalAmount);
            redirectAttributes.addFlashAttribute("message", "Cập nhật phí thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Cập nhật phí thất bại: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/fees";
    }

    private BigDecimal parseAmount(String input) {
        if (input == null || input.isBlank()) {
            return BigDecimal.ZERO;
        }
        String normalized = input.replace(".", "");
        return new BigDecimal(normalized);
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return "";
        }
        return String.format("%,d", amount.longValue()).replace(',', '.');
    }

    @GetMapping("/{id}/delete")
    public String deleteFee(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            feeService.getFee(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phí với ID: " + id));
            feeService.deleteFee(id);
            redirectAttributes.addFlashAttribute("message", "Xoá phí thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Xoá phí thất bại: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/fees";
    }

    // search handled by listFees
}
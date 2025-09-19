package com.example.dorm.controller;

import com.example.dorm.model.Fee;
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
        PageRequest pageable = PageRequest.of(page, size);
        Page<Fee> feesPage = feeService.searchFees(search, pageable);
        model.addAttribute("feesPage", feesPage);
        model.addAttribute("pageNumbers", PageUtils.buildPageNumbers(feesPage));
        model.addAttribute("search", search);
        return "fees/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("fee", new Fee());
        model.addAttribute("contracts", contractService.getAllContracts());
        return "fees/form";
    }

    @PostMapping
    public String createFee(@RequestParam("amountInput") String amountInput,
                            @Valid @ModelAttribute Fee fee,
                            BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("contracts", contractService.getAllContracts());
            return "fees/form";
        }
        try {
            fee.setAmount(parseAmount(amountInput));
            feeService.createFee(fee);
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
        model.addAttribute("fee", fee);
        model.addAttribute("contracts", contractService.getAllContracts());
        return "fees/form";
    }

    @PostMapping("/{id}")
    public String updateFee(@PathVariable("id") Long id,
                            @RequestParam("amountInput") String amountInput,
                            @Valid @ModelAttribute Fee fee,
                            BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("contracts", contractService.getAllContracts());
            return "fees/form";
        }
        try {
            fee.setAmount(parseAmount(amountInput));
            feeService.updateFee(id, fee);
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
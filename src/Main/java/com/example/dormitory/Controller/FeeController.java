package com.example.dormitory. Controller;

import Model.Fee;
import Service.FeeService;
import Service.ContractService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/fees")
public class FeeController {
    @Autowired
    private FeeService feeService;
    @Autowired
    private ContractService contractService;

    @GetMapping
    public String listFees(@RequestParam(value = "search", required = false) String search,
                           @RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "size", defaultValue = "10") int size,
                           Model model) {
        try {
            var pageable = org.springframework.data.domain.PageRequest.of(page, size);
            var feesPage = feeService.searchFees(search, pageable);
            model.addAttribute("feesPage", feesPage);
            model.addAttribute("search", search);
            return "fees/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi lấy danh sách phí: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        try {
            model.addAttribute("fee", new Fee());
            model.addAttribute("contracts", contractService.getAllContracts(org.springframework.data.domain.Pageable.unpaged()).getContent());
            return "fees/form";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi hiển thị form tạo phí: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping
    public String createFee(@RequestParam("amountInput") String amountInput,
                            @Valid @ModelAttribute Fee fee,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("contracts", contractService.getAllContracts(org.springframework.data.domain.Pageable.unpaged()).getContent());
            return "fees/form";
        }
        try {
            fee.setAmount(parseAmount(amountInput));
            feeService.createFee(fee);
            return "redirect:/fees";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi lưu phí: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}")
    public String viewFee(@PathVariable("id") Long id, Model model) {
        try {
            Optional<Fee> feeOptional = feeService.getFee(id);
            if (feeOptional.isPresent()) {
                model.addAttribute("fee", feeOptional.get());
                return "fees/detail";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy phí với ID: " + id);
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi xem chi tiết phí: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}/edit")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        try {
            Optional<Fee> feeOptional = feeService.getFee(id);
            if (feeOptional.isPresent()) {
                model.addAttribute("fee", feeOptional.get());
                model.addAttribute("contracts", contractService.getAllContracts(org.springframework.data.domain.Pageable.unpaged()).getContent());
                return "fees/form";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy phí với ID: " + id);
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi hiển thị form chỉnh sửa: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/{id}")
    public String updateFee(@PathVariable("id") Long id,
                            @RequestParam("amountInput") String amountInput,
                            @Valid @ModelAttribute Fee fee,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("contracts", contractService.getAllContracts(org.springframework.data.domain.Pageable.unpaged()).getContent());
            return "fees/form";
        }
        try {
            fee.setAmount(parseAmount(amountInput));
            feeService.updateFee(id, fee);
            return "redirect:/fees";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi cập nhật phí: " + e.getMessage());
            return "error";
        }
    }

    private java.math.BigDecimal parseAmount(String input) {
        if (input == null || input.isBlank()) {
            return java.math.BigDecimal.ZERO;
        }
        String normalized = input.replace(".", "");
        return new java.math.BigDecimal(normalized);
    }

    @GetMapping("/{id}/delete")
    public String deleteFee(@PathVariable("id") Long id, Model model) {
        try {
            Optional<Fee> feeOptional = feeService.getFee(id);
            if (feeOptional.isPresent()) {
                feeService.deleteFee(id);
                return "redirect:/fees";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy phí với ID: " + id);
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi xoá phí: " + e.getMessage());
            return "error";
        }
    }

    // search handled by listFees
}

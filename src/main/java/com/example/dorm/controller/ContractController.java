package com.example.dorm.controller;

import com.example.dorm.model.Contract;
import com.example.dorm.service.ContractService;
import com.example.dorm.service.RoomService;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/contracts")
public class ContractController {

    private final ContractService contractService;
    private final RoomService roomService;

    public ContractController(ContractService contractService,
                              RoomService roomService) {
        this.contractService = contractService;
        this.roomService = roomService;
    }

    @GetMapping
    public String listContracts(@RequestParam(value = "search", required = false) String search,
                                @RequestParam(name = "page", defaultValue = "0") int page,
                                @RequestParam(name = "size", defaultValue = "10") int size,
                                Model model) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Contract> contractsPage = contractService.searchContracts(search, pageable);
        model.addAttribute("contractsPage", contractsPage);
        model.addAttribute("pageNumbers", PageUtils.buildPageNumbers(contractsPage));
        model.addAttribute("search", search);
        return "contracts/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("contract", new Contract());
        model.addAttribute("rooms", roomService.getAllRooms());
        return "contracts/form";
    }

    @PostMapping
    public String createContract(@Valid @ModelAttribute Contract contract, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("rooms", roomService.getAllRooms());
            return "contracts/form";
        }
        if (contract.getStudent() == null || contract.getStudent().getId() == null) {
            model.addAttribute("rooms", roomService.getAllRooms());
            model.addAttribute("errorMessage", "Vui lòng chọn sinh viên hợp lệ");
            return "contracts/form";
        }
        try {
            contractService.createContract(contract);
            redirectAttributes.addFlashAttribute("message", "Thêm hợp đồng thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Thêm hợp đồng thất bại: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/contracts";
    }

    @GetMapping("/{id}")
    public String viewContract(@PathVariable("id") Long id, Model model) {
        Contract contract = contractService.getContract(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hợp đồng với ID: " + id));
        model.addAttribute("contract", contract);
        return "contracts/detail";
    }

    @GetMapping("/{id}/edit")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Contract contract = contractService.getContract(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hợp đồng với ID: " + id));
        model.addAttribute("contract", contract);
        model.addAttribute("rooms", roomService.getAllRooms());
        return "contracts/form";
    }

    @PostMapping("/{id}")
    public String updateContract(@PathVariable("id") Long id, @Valid @ModelAttribute Contract contract, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("rooms", roomService.getAllRooms());
            return "contracts/form";
        }
        if (contract.getStudent() == null || contract.getStudent().getId() == null) {
            model.addAttribute("rooms", roomService.getAllRooms());
            model.addAttribute("errorMessage", "Vui lòng chọn sinh viên hợp lệ");
            return "contracts/form";
        }
        try {
            contractService.updateContract(id, contract);
            redirectAttributes.addFlashAttribute("message", "Cập nhật hợp đồng thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Cập nhật hợp đồng thất bại: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/contracts";
    }

    @GetMapping("/{id}/delete")
    public String deleteContract(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            contractService.getContract(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hợp đồng với ID: " + id));
            contractService.deleteContract(id);
            redirectAttributes.addFlashAttribute("message", "Xoá hợp đồng thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Xoá hợp đồng thất bại: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/contracts";
    }

    @GetMapping(value = "/search", produces = "application/json")
    @ResponseBody
    public List<Map<String, Object>> autocomplete(@RequestParam("term") String term) {
        Page<Contract> contractsPage = contractService.searchContractsAutocomplete(term, PageRequest.of(0, 10));
        return contractsPage.getContent().stream()
                .map(contract -> {
                    String label = contract.getStudent() != null
                            ? contract.getId() + " - " + contract.getStudent().getCode() + " - " + contract.getStudent().getName()
                            : String.valueOf(contract.getId());
                    return Map.<String, Object>of("id", contract.getId(), "label", label);
                })
                .toList();
    }

    @GetMapping(value = "/by-room/{roomId}", produces = "application/json")
    @ResponseBody
    public List<Map<String, Object>> contractsByRoom(@PathVariable("roomId") Long roomId) {
        return contractService.getContractsByRoom(roomId).stream()
                .map(contract -> {
                    String label = contract.getStudent() != null
                            ? contract.getStudent().getCode() + " - " + contract.getStudent().getName()
                            : "Hợp đồng #" + contract.getId();
                    return Map.<String, Object>of(
                            "id", contract.getId(),
                            "label", label,
                            "roomId", contract.getRoom() != null ? contract.getRoom().getId() : null
                    );
                })
                .toList();
    }

    // search handled by listContracts
}
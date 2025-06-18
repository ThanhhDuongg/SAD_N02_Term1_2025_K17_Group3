package Controller;

import Model.Fee;
import Repository.FeeRepository;
import Repository.ContractRepository;
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
    private FeeRepository feeRepository;
    @Autowired
    private ContractRepository contractRepository;

    @GetMapping
    public String listFees(Model model) {
        try {
            model.addAttribute("fees", feeRepository.findAll());
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
            model.addAttribute("contracts", contractRepository.findAll());
            return "fees/form";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi hiển thị form tạo phí: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping
    public String createFee(@Valid @ModelAttribute Fee fee, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("contracts", contractRepository.findAll());
            return "fees/form";
        }
        try {
            feeRepository.save(fee);
            return "redirect:/fees";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi lưu phí: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}")
    public String viewFee(@PathVariable Long id, Model model) {
        try {
            Optional<Fee> feeOptional = feeRepository.findById(id);
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
    public String showUpdateForm(@PathVariable Long id, Model model) {
        try {
            Optional<Fee> feeOptional = feeRepository.findById(id);
            if (feeOptional.isPresent()) {
                model.addAttribute("fee", feeOptional.get());
                model.addAttribute("contracts", contractRepository.findAll());
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
    public String updateFee(@PathVariable Long id, @Valid @ModelAttribute Fee fee, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("contracts", contractRepository.findAll());
            return "fees/form";
        }
        try {
            Optional<Fee> feeOptional = feeRepository.findById(id);
            if (feeOptional.isPresent()) {
                Fee existingFee = feeOptional.get();
                existingFee.setAmount(fee.getAmount());
                existingFee.setContract(fee.getContract());
                existingFee.setType(fee.getType());
                existingFee.setDueDate(fee.getDueDate());
                existingFee.setPaymentStatus(fee.getPaymentStatus());
                feeRepository.save(existingFee);
                return "redirect:/fees";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy phí với ID: " + id);
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi cập nhật phí: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteFee(@PathVariable Long id, Model model) {
        try {
            Optional<Fee> feeOptional = feeRepository.findById(id);
            if (feeOptional.isPresent()) {
                feeRepository.deleteById(id);
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

    @GetMapping("/search")
    public String searchFees(@RequestParam("search") String search, Model model) {
        try {
            if (search == null || search.trim().isEmpty()) {
                model.addAttribute("fees", feeRepository.findAll());
            } else {
                try {
                    Long id = Long.parseLong(search);
                    Optional<Fee> feeOptional = feeRepository.findById(id);
                    if (feeOptional.isPresent()) {
                        model.addAttribute("fees", java.util.List.of(feeOptional.get()));
                    } else {
                        model.addAttribute("fees", java.util.Collections.emptyList());
                        model.addAttribute("errorMessage", "Không tìm thấy phí với ID: " + id);
                    }
                } catch (NumberFormatException e) {
                    model.addAttribute("fees", java.util.Collections.emptyList());
                    model.addAttribute("errorMessage", "Vui lòng nhập ID hợp lệ (số).");
                }
            }
            model.addAttribute("search", search);
            return "fees/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tìm kiếm phí: " + e.getMessage());
            return "error";
        }
    }
}

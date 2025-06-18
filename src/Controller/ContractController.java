package Controller;

import Model.Contract;
import Repository.ContractRepository;
import Repository.StudentRepository;
import Repository.RoomRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/contracts")
public class ContractController {
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private RoomRepository roomRepository;

    @GetMapping
    public String listContracts(Model model) {
        try {
            model.addAttribute("contracts", contractRepository.findAll());
            return "contracts/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi lấy danh sách hợp đồng: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        try {
            model.addAttribute("contract", new Contract());
            model.addAttribute("students", studentRepository.findAll());
            model.addAttribute("rooms", roomRepository.findAll());
            return "contracts/form";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi hiển thị form tạo hợp đồng: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping
    public String createContract(@Valid @ModelAttribute Contract contract, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("students", studentRepository.findAll());
            model.addAttribute("rooms", roomRepository.findAll());
            return "contracts/form";
        }
        try {
            contractRepository.save(contract);
            return "redirect:/contracts";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi lưu hợp đồng: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}")
    public String viewContract(@PathVariable Long id, Model model) {
        try {
            Optional<Contract> contractOptional = contractRepository.findById(id);
            if (contractOptional.isPresent()) {
                model.addAttribute("contract", contractOptional.get());
                return "contracts/detail";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy hợp đồng với ID: " + id);
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi xem hợp đồng: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}/edit")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        try {
            Optional<Contract> contractOptional = contractRepository.findById(id);
            if (contractOptional.isPresent()) {
                model.addAttribute("contract", contractOptional.get());
                model.addAttribute("students", studentRepository.findAll());
                model.addAttribute("rooms", roomRepository.findAll());
                return "contracts/form";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy hợp đồng với ID: " + id);
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tải form chỉnh sửa: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/{id}")
    public String updateContract(@PathVariable Long id, @Valid @ModelAttribute Contract contract, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("students", studentRepository.findAll());
            model.addAttribute("rooms", roomRepository.findAll());
            return "contracts/form";
        }
        try {
            Optional<Contract> contractOptional = contractRepository.findById(id);
            if (contractOptional.isPresent()) {
                Contract existingContract = contractOptional.get();
                existingContract.setStudent(contract.getStudent());
                existingContract.setRoom(contract.getRoom());
                existingContract.setStartDate(contract.getStartDate());
                existingContract.setEndDate(contract.getEndDate());
                existingContract.setStatus(contract.getStatus());
                contractRepository.save(existingContract);
                return "redirect:/contracts";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy hợp đồng với ID: " + id);
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi cập nhật hợp đồng: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteContract(@PathVariable Long id, Model model) {
        try {
            Optional<Contract> contractOptional = contractRepository.findById(id);
            if (contractOptional.isPresent()) {
                contractRepository.deleteById(id);
                return "redirect:/contracts";
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy hợp đồng với ID: " + id);
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi xoá hợp đồng: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/search")
    public String searchContracts(@RequestParam("search") String search, Model model) {
        try {
            if (search == null || search.trim().isEmpty()) {
                model.addAttribute("contracts", contractRepository.findAll());
            } else {
                model.addAttribute("contracts",
                    contractRepository.findByStudent_NameContainingIgnoreCaseOrRoom_NumberContainingIgnoreCaseOrStatusContainingIgnoreCase(
                        search, search, search));
            }
            model.addAttribute("search", search);
            return "contracts/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tìm kiếm hợp đồng: " + e.getMessage());
            return "error";
        }
    }
}

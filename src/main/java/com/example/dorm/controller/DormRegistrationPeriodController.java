package com.example.dorm.controller;

import com.example.dorm.model.DormRegistrationPeriod;
import com.example.dorm.service.DormRegistrationPeriodService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/registrations/periods")
public class DormRegistrationPeriodController {

    private final DormRegistrationPeriodService dormRegistrationPeriodService;

    public DormRegistrationPeriodController(DormRegistrationPeriodService dormRegistrationPeriodService) {
        this.dormRegistrationPeriodService = dormRegistrationPeriodService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("periods", dormRegistrationPeriodService.findAll());
        model.addAttribute("periodForm", new DormRegistrationPeriod());
        model.addAttribute("activePeriod", dormRegistrationPeriodService.getOpenPeriod().orElse(null));
        return "registrations/periods";
    }

    @PostMapping
    public String openPeriod(@ModelAttribute("periodForm") DormRegistrationPeriod period,
                             RedirectAttributes redirectAttributes) {
        try {
            dormRegistrationPeriodService.openPeriod(period);
            redirectAttributes.addFlashAttribute("message", "Đã mở đợt đăng ký mới thành công.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", "Không thể mở đợt đăng ký: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/registrations/periods";
    }

    @PostMapping("/{id}/close")
    public String closePeriod(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            dormRegistrationPeriodService.closePeriod(id);
            redirectAttributes.addFlashAttribute("message", "Đã đóng đợt đăng ký.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", "Không thể đóng đợt đăng ký: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/registrations/periods";
    }
}

package com.example.dorm.controller;

import com.example.dorm.model.Building;
import com.example.dorm.service.BuildingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/buildings")
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping
    public String listBuildings(Model model) {
        model.addAttribute("buildingSummaries", buildingService.getBuildingSummaries());
        return "buildings/list";
    }

    @GetMapping("/{id}")
    public String viewBuilding(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("detail", buildingService.getBuildingDetail(id));
            return "buildings/detail";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/buildings";
        }
    }

    @GetMapping(value = "/options", produces = "application/json")
    @ResponseBody
    public List<Map<String, Object>> buildingOptions() {
        return buildingService.getAllBuildings().stream()
                .map(building -> Map.<String, Object>of(
                        "id", building.getId(),
                        "code", building.getCode(),
                        "name", building.getName()
                ))
                .toList();
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("building", new Building());
        model.addAttribute("isEdit", false);
        return "buildings/form";
    }

    @PostMapping
    public String createBuilding(@ModelAttribute("building") Building building,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            buildingService.createBuilding(building);
            redirectAttributes.addFlashAttribute("message", "Thêm tòa nhà thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/buildings";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("isEdit", false);
            return "buildings/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Building building = buildingService.getRequiredBuilding(id);
            model.addAttribute("building", building);
            model.addAttribute("isEdit", true);
            return "buildings/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Không tìm thấy tòa nhà");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/buildings";
        }
    }

    @PostMapping("/{id}")
    public String updateBuilding(@PathVariable Long id,
                                 @ModelAttribute("building") Building building,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            buildingService.updateBuilding(id, building);
            redirectAttributes.addFlashAttribute("message", "Cập nhật tòa nhà thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
            return "redirect:/buildings";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("isEdit", true);
            return "buildings/form";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteBuilding(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            buildingService.deleteBuilding(id);
            redirectAttributes.addFlashAttribute("message", "Xóa tòa nhà thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Không thể xóa tòa nhà: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/buildings";
    }
}

package me.owlsleep.owlab.controller;

import me.owlsleep.owlab.service.AdminDashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final AdminDashboardService adminDashboardService;

    public AdminController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("dashboard", adminDashboardService.getDashboardData());
        return "admin/dashboard";
    }
}

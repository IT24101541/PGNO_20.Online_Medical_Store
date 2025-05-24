package com.getmed.pgno20.controller;

import com.getmed.pgno20.model.Admin;
import com.getmed.pgno20.model.Medicine;
import com.getmed.pgno20.model.Order;
import com.getmed.pgno20.service.AdminService;
import com.getmed.pgno20.service.MedicineService;
import com.getmed.pgno20.service.OrderQueueService;
import com.getmed.pgno20.util.MedicineFileUtil;
import com.getmed.pgno20.util.OrderFileUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController extends MedicineFileUtil {

    private final AdminService adminService;
    private final OrderQueueService orderQueueService;
    private final MedicineService medicineService;

    @Autowired
    public AdminController(AdminService adminService, OrderQueueService orderQueueService, MedicineService medicineService) {
        this.adminService = adminService;
        this.orderQueueService = orderQueueService;
        this.medicineService = medicineService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(@RequestParam(required = false) String section,
                                 @RequestParam(required = false) String action,
                                 @RequestParam(required = false) Integer id,
                                 HttpSession session,
                                 Model model) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/login";
        }
        model.addAttribute("section", section != null ? section : "user");
        model.addAttribute("action", action);

        Admin admin = adminService.getAdminByEmail(loggedInAdmin.getEmail());
        if (admin == null) {
            admin = new Admin();
            admin.setName("Unknown");
            admin.setAddress("N/A");
            admin.setContactNo("N/A");
            admin.setEmail("N/A");
        }
        model.addAttribute("admin", admin);

        if ("user".equals(section)) {
            if ("delete".equals(action) && id != null) {
                Admin deleteAdmin = adminService.getAdminById(id);
                model.addAttribute("admin", deleteAdmin != null ? deleteAdmin : admin);
            }
        } else if ("order".equals(section)) {
            List<Order> orders = orderQueueService.getAllOrders();
            for (Order order : orders) {
                order.setMedicineNamesString();
            }
            model.addAttribute("orders", orders);
        } else if ("shop".equals(section)) {
            List<Medicine> medicines = MedicineFileUtil.readMedicinesFromFile();
            model.addAttribute("medicines", medicines);
            if ("update".equals(action) && id != null) {
                Medicine medicine = medicines.stream().filter(m -> m.getId() == id).findFirst().orElse(null);
                model.addAttribute("medicine", medicine != null ? medicine : new Medicine());
            } else if ("delete".equals(action) && id != null) {
                Medicine medicine = medicines.stream().filter(m -> m.getId() == id).findFirst().orElse(null);
                model.addAttribute("medicine", medicine != null ? medicine : new Medicine());
            } else if ("create".equals(action)) {
                model.addAttribute("medicine", new Medicine());
            }
        }
        return "admin_dashboard";
    }

    @PostMapping("/update")
    public String updateAdmin(@ModelAttribute Admin admin, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/login";
        }
        adminService.updateAdmin(admin);
        return "redirect:/admin/dashboard?section=user";
    }

    @PostMapping("/delete")
    public String deleteAdmin(@RequestParam("id") int adminId, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/login";
        }
        boolean deleted = adminService.deleteAdmin(adminId);
        if (deleted) {
            session.invalidate();
            return "redirect:/login";
        }
        return "redirect:/admin/dashboard?section=user";
    }

    @PostMapping("/shop/save")
    public String saveMedicine(@ModelAttribute Medicine medicine, @RequestParam(required = false) MultipartFile image, HttpSession session) throws IOException {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/login";
        }
        medicineService.saveMedicine(medicine, image);
        return "redirect:/admin/dashboard?section=shop";
    }

    @PostMapping("/shop/update")
    public String updateMedicine(@ModelAttribute Medicine medicine, @RequestParam(required = false) MultipartFile image, HttpSession session) throws IOException {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/login";
        }
        medicineService.updateMedicine(medicine, image);
        return "redirect:/admin/dashboard?section=shop";
    }

    @PostMapping("/shop/delete")
    public String deleteMedicine(@RequestParam("id") int id, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/login";
        }
        medicineService.deleteMedicine(id);
        return "redirect:/admin/dashboard?section=shop";
    }

    @PostMapping("/orders/toggle/{id}")
    public String toggleOrderStatus(@PathVariable int id, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/login";
        }
        List<Order> orders = orderQueueService.getAllOrders();
        for (Order order : orders) {
            if (order.getId() == id) {
                order.setStatus(order.getStatus().equals("PENDING") ? "COMPLETE" : "PENDING");
                orderQueueService.updateOrder(order);
                break;
            }
        }
        return "redirect:/admin/dashboard?section=order";
    }
}
package com.getmed.pgno20.controller;

import com.getmed.pgno20.model.Admin;
import com.getmed.pgno20.model.Medicine;
import com.getmed.pgno20.model.Order;
import com.getmed.pgno20.service.AdminService;
import com.getmed.pgno20.service.OrderQueueService;
import com.getmed.pgno20.util.MedicineFileUtil;
import com.getmed.pgno20.util.OrderFileUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController extends MedicineFileUtil {

    private final AdminService adminService;
    private final OrderQueueService orderQueueService;

    private static final String UPLOAD_DIR = "uploads"; // Directory in the project root

    @Autowired
    public AdminController(AdminService adminService, OrderQueueService orderQueueService) {
        this.adminService = adminService;
        this.orderQueueService = orderQueueService;
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
            // Preprocess medicineNamesString for each order
            for (Order order : orders) {
                order.setMedicineNamesString(); // Populate the string from medicineQuantities
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
        List<Medicine> medicines = MedicineFileUtil.readMedicinesFromFile();
        int maxId = medicines.stream().mapToInt(Medicine::getId).max().orElse(0) + 1;
        medicine.setId(maxId);

        if (image != null && !image.isEmpty()) {
            // Create the uploads directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate a unique filename
            String fileName = "medicine_" + maxId + ".jpg";
            String uniqueFileName = generateUniqueFileName(fileName, uploadPath);
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(image.getInputStream(), filePath);

            // Set the image URL for serving in the web app
            medicine.setImageUrl("/" + UPLOAD_DIR + "/" + uniqueFileName);
        }

        medicines.add(medicine);
        MedicineFileUtil.writeMedicinesToFile(medicines);
        return "redirect:/admin/dashboard?section=shop";
    }

    @PostMapping("/shop/update")
    public String updateMedicine(@ModelAttribute Medicine medicine, @RequestParam(required = false) MultipartFile image, HttpSession session) throws IOException {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/login";
        }
        List<Medicine> medicines = MedicineFileUtil.readMedicinesFromFile();
        medicines.removeIf(m -> m.getId() == medicine.getId());

        if (image != null && !image.isEmpty()) {
            // Create the uploads directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate a unique filename
            String fileName = "medicine_" + medicine.getId() + ".jpg";
            String uniqueFileName = generateUniqueFileName(fileName, uploadPath);
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(image.getInputStream(), filePath);

            // Set the image URL for serving in the web app
            medicine.setImageUrl("/" + UPLOAD_DIR + "/" + uniqueFileName);
        }

        medicines.add(medicine);
        MedicineFileUtil.writeMedicinesToFile(medicines);
        return "redirect:/admin/dashboard?section=shop";
    }

    @PostMapping("/shop/delete")
    public String deleteMedicine(@RequestParam("id") int id, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        if (loggedInAdmin == null) {
            return "redirect:/login";
        }
        List<Medicine> medicines = MedicineFileUtil.readMedicinesFromFile();
        medicines.removeIf(m -> m.getId() == id);
        MedicineFileUtil.writeMedicinesToFile(medicines);
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

    private String generateUniqueFileName(String originalFileName, Path uploadPath) {
        String fileName = originalFileName;
        String extension = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = fileName.substring(lastDotIndex);
            fileName = fileName.substring(0, lastDotIndex);
        }

        int counter = 1;
        Path filePath = uploadPath.resolve(originalFileName);
        while (Files.exists(filePath)) {
            String newFileName = fileName + "_" + counter + extension;
            filePath = uploadPath.resolve(newFileName);
            counter++;
        }
        return filePath.getFileName().toString();
    }
}
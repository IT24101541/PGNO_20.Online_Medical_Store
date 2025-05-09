package com.getmed.pgno20.controller;

import com.getmed.pgno20.model.Customer;
import com.getmed.pgno20.model.Medicine;
import com.getmed.pgno20.model.Order;
import com.getmed.pgno20.model.User;
import com.getmed.pgno20.service.CustomerService;
import com.getmed.pgno20.service.AdminService;
import com.getmed.pgno20.service.OrderQueueService;
import com.getmed.pgno20.util.MedicineFileUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class CustomerController {

    private final CustomerService customerService;
    private final AdminService adminService;
    private final OrderQueueService orderQueueService;
    private static final String UPLOAD_DIR = "uploads";

    @Autowired
    public CustomerController(CustomerService customerService, AdminService adminService, OrderQueueService orderQueueService) {
        this.customerService = customerService;
        this.adminService = adminService;
        this.orderQueueService = orderQueueService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        if ("customer".equalsIgnoreCase(user.getRole())) {
            Customer customer = new Customer();
            // Check if a customer with the same email already exists
            if (customerService.getCustomerByEmail(user.getEmail()) != null) {
                model.addAttribute("error", "A customer with this email already exists.");
                return "register";
            }
            // Assign a new ID based on the highest existing ID
            List<Customer> allCustomers = customerService.getAllCustomers();
            int maxId = allCustomers.stream().mapToInt(Customer::getId).max().orElse(0);
            customer.setId(maxId + 1);
            customer.setName(user.getName());
            customer.setAddress(user.getAddress());
            customer.setContactNo(user.getContactNo());
            customer.setEmail(user.getEmail());
            customer.setPassword(user.getPassword());
            customerService.saveUser(customer);
            model.addAttribute("message", "Registration successful as Customer. Please log in.");
        } else if ("admin".equalsIgnoreCase(user.getRole())) {
            User admin = new User();
            admin.setEmail(user.getEmail());
            admin.setPassword(user.getPassword());
            admin.setName(user.getName());
            admin.setRole("admin");
            adminService.saveUser(admin);
            model.addAttribute("message", "Registration successful as Admin. Please log in.");
        } else {
            model.addAttribute("error", "Invalid role selected.");
            return "register";
        }
        return "redirect:/login";
    }

    @GetMapping("/customer/dashboard")
    public String customerDashboard(
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String action,
            @RequestParam(required = false, defaultValue = "none") String sort,
            @RequestParam(required = false) Integer id,
            HttpSession session,
            Model model) {
        String email = (String) session.getAttribute("loggedInUser");
        if (email == null) return "redirect:/login";

        Customer customer = customerService.getCustomerByEmail(email);
        if (customer == null) {
            model.addAttribute("error", "Customer not found.");
            return "redirect:/login";
        }

        model.addAttribute("customer", customer);
        model.addAttribute("section", section != null ? section : "user");
        model.addAttribute("action", action);

        @SuppressWarnings("unchecked")
        List<Medicine> selectedMedicines = (List<Medicine>) session.getAttribute("selectedMedicines");
        if (selectedMedicines == null) {
            selectedMedicines = new ArrayList<>();
            session.setAttribute("selectedMedicines", selectedMedicines);
        } else {
            // Validate the list to handle deserialization issues
            try {
                // Attempt to access an element to trigger any deserialization issues
                if (!selectedMedicines.isEmpty()) selectedMedicines.get(0);
            } catch (Exception e) {
                // If deserialization fails, clear the invalid session data
                selectedMedicines = new ArrayList<>();
                session.setAttribute("selectedMedicines", selectedMedicines);
                System.err.println("Failed to deserialize selectedMedicines, resetting to empty list: " + e.getMessage());
            }
        }

        if ("shop".equals(section)) {
            List<Medicine> medicines = MedicineFileUtil.readMedicinesFromFile();
            quickSort(medicines, 0, medicines.size() - 1, "high".equals(sort));
            model.addAttribute("medicines", medicines);
            model.addAttribute("selectedMedicines", selectedMedicines);

            if ("add".equals(action) && id != null) {
                Medicine medicineToAdd = medicines.stream()
                        .filter(m -> m.getId() == id)
                        .findFirst()
                        .orElse(null);
                if (medicineToAdd != null && !selectedMedicines.contains(medicineToAdd)) {
                    selectedMedicines.add(medicineToAdd);
                }
                return "redirect:/customer/dashboard?section=shop";
            }

            if ("remove".equals(action) && id != null) {
                selectedMedicines.removeIf(m -> m.getId() == id);
                return "redirect:/customer/dashboard?section=shop";
            }

            if ("send-to-order".equals(action)) {
                if (!selectedMedicines.isEmpty()) {
                    return "redirect:/customer/dashboard?section=order&action=create";
                }
                return "redirect:/customer/dashboard?section=shop";
            }
        } else if ("order".equals(section)) {
            List<Order> orders = orderQueueService.getAllOrders().stream()
                    .filter(o -> o.getCustomerEmail().equals(email))
                    .collect(Collectors.toList());
            model.addAttribute("orders", orders);

            if ("create".equals(action) || "edit".equals(action)) {
                Order order = new Order();
                if ("edit".equals(action) && id != null) {
                    order = orders.stream().filter(o -> o.getId() == id).findFirst().orElse(new Order());
                }
                order.setCustomerEmail(email);
                if (!selectedMedicines.isEmpty()) {
                    for (Medicine med : selectedMedicines) {
                        order.getMedicineQuantities().put(med.getName(), 1); // Default quantity
                    }
                    order.setTotalQuantity(selectedMedicines.size());
                    selectedMedicines.clear();
                    session.setAttribute("selectedMedicines", selectedMedicines);
                }
                model.addAttribute("order", order);
            }
        }

        return "customer_dashboard";
    }

    @PostMapping("/customer/dashboard")
    public String processOrder(
            @ModelAttribute Order order,
            @RequestParam(required = false) MultipartFile prescription,
            @RequestParam(required = false) MultipartFile receipt,
            HttpSession session,
            Model model) throws IOException {
        String email = (String) session.getAttribute("loggedInUser");
        if (email == null) return "redirect:/login";

        Customer customer = customerService.getCustomerByEmail(email);
        if (customer == null) return "redirect:/login";

        // Validate order creation per truth table
        boolean hasMedicine = !order.getMedicineQuantities().isEmpty();
        boolean hasPrescription = prescription != null && !prescription.isEmpty();
        boolean hasReceipt = receipt != null && !receipt.isEmpty();

        if (!(hasMedicine || hasPrescription) && !hasReceipt) {
            model.addAttribute("error", "Order must include at least one medicine, prescription, or receipt.");
            return "redirect:/customer/dashboard?section=order&action=create";
        }

        // Handle file uploads
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        if (hasPrescription) {
            String fileName = "prescription_" + System.currentTimeMillis() + ".pdf";
            Files.copy(prescription.getInputStream(), uploadPath.resolve(fileName));
            order.setPrescriptionUrl("/" + UPLOAD_DIR + "/" + fileName);
        }
        if (hasReceipt) {
            String fileName = "receipt_" + System.currentTimeMillis() + ".pdf";
            Files.copy(receipt.getInputStream(), uploadPath.resolve(fileName));
            order.setReceiptUrl("/" + UPLOAD_DIR + "/" + fileName);
        }

        order.setCustomerEmail(email);
        order.setOrderDate(LocalDate.now().toString());
        order.setTotalQuantity(order.getMedicineQuantities().values().stream().mapToInt(Integer::intValue).sum());
        orderQueueService.addOrder(order);

        return "redirect:/customer/dashboard?section=order";
    }

    @GetMapping("/customer/deleteOrder")
    public String deleteOrder(@RequestParam("id") int id, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";
        orderQueueService.deleteOrder(id);
        return "redirect:/customer/dashboard?section=order";
    }

    private void quickSort(List<Medicine> arr, int low, int high, boolean descending) {
        if (low < high) {
            int pi = partition(arr, low, high, descending);
            quickSort(arr, low, pi - 1, descending);
            quickSort(arr, pi + 1, high, descending);
        }
    }

    private int partition(List<Medicine> arr, int low, int high, boolean descending) {
        double pivot = arr.get(high).getPrice();
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if ((descending && arr.get(j).getPrice() >= pivot) || (!descending && arr.get(j).getPrice() <= pivot)) {
                i++;
                Collections.swap(arr, i, j);
            }
        }
        Collections.swap(arr, i + 1, high);
        return i + 1;
    }
}
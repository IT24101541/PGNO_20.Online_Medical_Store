package com.getmed.pgno20.controller;

import com.getmed.pgno20.model.Medicine;
import com.getmed.pgno20.model.Order;
import com.getmed.pgno20.service.OrderQueueService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderQueueService orderQueueService;

    @GetMapping
    public String viewOrders(HttpSession session, Model model) {
        String email = (String) session.getAttribute("loggedInUser");
        if (email == null) return "redirect:/login";

        List<Order> orders = orderQueueService.getAllOrders().stream()
                .filter(order -> order.getCustomerEmail().equals(email))
                .toList();
        model.addAttribute("orders", orders);
        model.addAttribute("section", "order");
        return "customer_dashboard";
    }

    private String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        File dest = new File("src/main/resources/data/uploads/" + fileName);
        File directory = dest.getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            System.err.println("Failed to create directory: " + directory.getAbsolutePath());
        }
        file.transferTo(dest);
        return "/data/uploads/" + fileName;
    }

    @GetMapping("/new")
    public String showOrderForm(HttpSession session, Model model) {
        String email = (String) session.getAttribute("loggedInUser");
        if (email == null) return "redirect:/login";

        Order order = new Order();
        order.setCustomerEmail(email);
        order.setOrderDate(java.time.LocalDate.now().toString());
        @SuppressWarnings("unchecked")
        List<Medicine> selectedMedicines = (List<Medicine>) session.getAttribute("selectedMedicines");
        model.addAttribute("order", order);
        model.addAttribute("selectedMedicines", selectedMedicines != null ? selectedMedicines : new ArrayList<>());
        model.addAttribute("section", "order");
        model.addAttribute("action", "create");
        return "customer_dashboard";
    }

    @PostMapping("/save")
    public String saveOrder(@ModelAttribute Order order,
                            @RequestParam(required = false) MultipartFile prescription,
                            @RequestParam(required = false) MultipartFile receipt,
                            @RequestParam(name = "quantities", required = false) Integer[] quantities,
                            HttpSession session, Model model) {
        String email = (String) session.getAttribute("loggedInUser");
        if (email == null) return "redirect:/login";

        try {
            @SuppressWarnings("unchecked")
            List<Medicine> selectedMedicines = (List<Medicine>) session.getAttribute("selectedMedicines");
            if (selectedMedicines != null && !selectedMedicines.isEmpty()) {
                for (int i = 0; i < selectedMedicines.size(); i++) {
                    int quantity = (quantities != null && i < quantities.length && quantities[i] != null) ? quantities[i] : 1;
                    order.getMedicineQuantities().put(selectedMedicines.get(i).getName(), quantity);
                }
                order.setTotalQuantity(order.getMedicineQuantities().values().stream().mapToInt(Integer::intValue).sum());
            } else {
                order.setTotalQuantity(0);
            }

            if (prescription != null && !prescription.isEmpty()) {
                order.setPrescriptionUrl(saveFile(prescription));
            }
            if (receipt != null && !receipt.isEmpty()) {
                order.setReceiptUrl(saveFile(receipt));
            }
            order.setStatus("PENDING");
            orderQueueService.addOrder(order);
            session.removeAttribute("selectedMedicines"); // Clear cart after processing
            return "redirect:/customer/dashboard?section=order";
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload file: " + e.getMessage());
            model.addAttribute("order", order);
            model.addAttribute("selectedMedicines", session.getAttribute("selectedMedicines"));
            model.addAttribute("section", "order");
            model.addAttribute("action", "create");
            return "customer_dashboard";
        }
    }

    @GetMapping("/edit/{id}")
    public String editOrder(@PathVariable int id, HttpSession session, Model model) {
        String email = (String) session.getAttribute("loggedInUser");
        if (email == null) return "redirect:/login";

        List<Order> orders = orderQueueService.getAllOrders();
        Order order = orders.stream()
                .filter(o -> o.getId() == id && o.getCustomerEmail().equals(email))
                .findFirst()
                .orElse(null);
        if (order == null) return "redirect:/customer/dashboard?section=order";

        model.addAttribute("order", order);
        @SuppressWarnings("unchecked")
        List<Medicine> selectedMedicines = (List<Medicine>) session.getAttribute("selectedMedicines");
        model.addAttribute("selectedMedicines", selectedMedicines != null ? selectedMedicines : new ArrayList<>());
        model.addAttribute("section", "order");
        model.addAttribute("action", "edit");
        return "customer_dashboard";
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable int id, HttpSession session) {
        String email = (String) session.getAttribute("loggedInUser");
        if (email == null) return "redirect:/login";

        orderQueueService.deleteOrder(id);
        return "redirect:/customer/dashboard?section=order";
    }

    @GetMapping("/download/{fileUrl}")
    public String downloadFile(@PathVariable String fileUrl, Model model) {
        model.addAttribute("fileUrl", fileUrl);
        return "download"; // Create a download.html if not exists (simple redirect to file)
    }
}
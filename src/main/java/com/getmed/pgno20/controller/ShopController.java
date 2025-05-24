package com.getmed.pgno20.controller;

import com.getmed.pgno20.model.Admin;
import com.getmed.pgno20.model.Medicine;
import com.getmed.pgno20.util.MedicineFileUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/shop")
public class ShopController extends MedicineFileUtil {

    private static final String UPLOAD_DIR = "uploads"; // Directory in the project root

    @GetMapping("/customer-view")
    public String viewMedicines(@RequestParam(required = false) String sort, HttpSession session, Model model) {
        String email = (String) session.getAttribute("loggedInUser");
        if (email == null) return "redirect:/login";

        List<Medicine> medicines = MedicineFileUtil.readMedicinesFromFile();
        if ("high".equals(sort)) {
            quickSort(medicines, 0, medicines.size() - 1, Comparator.comparingDouble(Medicine::getPrice).reversed());
        } else if ("low".equals(sort)) {
            quickSort(medicines, 0, medicines.size() - 1, Comparator.comparingDouble(Medicine::getPrice));
        }

        @SuppressWarnings("unchecked")
        List<Medicine> selectedMedicines = (List<Medicine>) session.getAttribute("selectedMedicines");
        if (selectedMedicines == null) {
            selectedMedicines = new ArrayList<>();
            session.setAttribute("selectedMedicines", selectedMedicines);
        }

        Set<Integer> selectedMedicineIds = selectedMedicines.stream()
                .map(Medicine::getId)
                .collect(Collectors.toSet());

        model.addAttribute("medicines", medicines);
        model.addAttribute("selectedMedicines", selectedMedicines);
        model.addAttribute("selectedMedicineIds", selectedMedicineIds);
        model.addAttribute("section", "shop");
        return "customer_dashboard";
    }

    @GetMapping("/add/{id}")
    public String addToOrder(@PathVariable int id, HttpSession session) {
        String email = (String) session.getAttribute("loggedInUser");
        if (email == null) return "redirect:/login";

        List<Medicine> medicines = MedicineFileUtil.readMedicinesFromFile();
        Medicine medicine = medicines.stream()
                .filter(m -> m.getId() == id)
                .findFirst()
                .orElse(null);
        if (medicine == null) return "redirect:/shop/customer-view";

        @SuppressWarnings("unchecked")
        List<Medicine> selectedMedicines = (List<Medicine>) session.getAttribute("selectedMedicines");
        if (selectedMedicines == null) {
            selectedMedicines = new ArrayList<>();
            session.setAttribute("selectedMedicines", selectedMedicines);
        }
        if (!selectedMedicines.contains(medicine)) {
            selectedMedicines.add(medicine);
        }
        return "redirect:/shop/customer-view";
    }

    @GetMapping("/remove/{id}")
    public String removeFromOrder(@PathVariable int id, HttpSession session) {
        String email = (String) session.getAttribute("loggedInUser");
        if (email == null) return "redirect:/login";

        @SuppressWarnings("unchecked")
        List<Medicine> selectedMedicines = (List<Medicine>) session.getAttribute("selectedMedicines");
        if (selectedMedicines != null) {
            selectedMedicines.removeIf(medicine -> medicine.getId() == id);
        }
        return "redirect:/shop/customer-view";
    }

    @GetMapping("/send-to-order")
    public String sendToOrder(HttpSession session) {
        String email = (String) session.getAttribute("loggedInUser");
        if (email == null) return "redirect:/login";

        @SuppressWarnings("unchecked")
        List<Medicine> selectedMedicines = (List<Medicine>) session.getAttribute("selectedMedicines");
        if (selectedMedicines == null || selectedMedicines.isEmpty()) {
            return "redirect:/shop/customer-view";
        }
        return "redirect:/customer/dashboard?section=order&action=create";
    }

    @PostMapping("/update")
    public String updateMedicine(@ModelAttribute Medicine medicine, @RequestParam(required = false) MultipartFile image, HttpSession session) throws IOException {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin"); // Assuming admin-only access
        if (loggedInAdmin == null) {
            return "redirect:/login";
        }
        List<Medicine> medicines = MedicineFileUtil.readMedicinesFromFile();
        medicines.removeIf(m -> m.getId() == medicine.getId());

        if (image != null && !image.isEmpty()) {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = "medicine_" + medicine.getId() + ".jpg";
            String uniqueFileName = generateUniqueFileName(fileName, uploadPath);
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(image.getInputStream(), filePath);

            medicine.setImageUrl("/" + UPLOAD_DIR + "/" + uniqueFileName);
        }

        medicines.add(medicine);
        MedicineFileUtil.writeMedicinesToFile(medicines);
        return "redirect:/shop/customer-view";
    }

    @PostMapping("/delete")
    public String deleteMedicine(@RequestParam("id") int id, HttpSession session) {
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin"); // Assuming admin-only access
        if (loggedInAdmin == null) {
            return "redirect:/login";
        }
        List<Medicine> medicines = MedicineFileUtil.readMedicinesFromFile();
        medicines.removeIf(m -> m.getId() == id);
        MedicineFileUtil.writeMedicinesToFile(medicines);
        return "redirect:/shop/customer-view";
    }

    // QuickSort implementation (unchanged)
    private void quickSort(List<Medicine> list, int low, int high, Comparator<Medicine> comparator) {
        if (low < high) {
            int pi = partition(list, low, high, comparator);
            quickSort(list, low, pi - 1, comparator);
            quickSort(list, pi + 1, high, comparator);
        }
    }

    private int partition(List<Medicine> list, int low, int high, Comparator<Medicine> comparator) {
        Medicine pivot = list.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (comparator.compare(list.get(j), pivot) <= 0) {
                i++;
                Medicine temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
            }
        }
        Medicine temp = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, temp);
        return i + 1;
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
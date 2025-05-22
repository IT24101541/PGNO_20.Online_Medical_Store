package com.getmed.pgno20.controller;

import com.getmed.pgno20.model.Medicine;
import com.getmed.pgno20.util.MedicineFileUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/shop")
public class ShopController {

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
        if (medicine == null) return "redirect:/customer/dashboard?section=shop";

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
            return "redirect:/customer/dashboard?section=shop";
        }
        return "redirect:/customer/dashboard?section=order&action=create";
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
}
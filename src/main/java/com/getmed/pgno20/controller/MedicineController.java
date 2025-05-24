package com.getmed.pgno20.controller;

import com.getmed.pgno20.model.Medicine;
import com.getmed.pgno20.util.MedicineFileUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/medicine")
public class MedicineController extends MedicineFileUtil {

    private static final String UPLOAD_DIR = "uploads"; // Directory in the project root

    @PostMapping("/update")
    public String updateMedicine(@ModelAttribute Medicine medicine, @RequestParam(required = false) MultipartFile image, HttpSession session) throws IOException {
        // Check if admin is logged in
        if (session.getAttribute("loggedInAdmin") == null) {
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
        return "redirect:/admin/dashboard?section=shop";
    }

    @PostMapping("/delete")
    public String deleteMedicine(@RequestParam("id") int id, HttpSession session) {
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }
        List<Medicine> medicines = MedicineFileUtil.readMedicinesFromFile();
        medicines.removeIf(m -> m.getId() == id);
        MedicineFileUtil.writeMedicinesToFile(medicines);
        return "redirect:/admin/dashboard?section=shop";
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
package com.getmed.pgno20.service;

import com.getmed.pgno20.model.Medicine;
import com.getmed.pgno20.util.MedicineFileUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class MedicineService extends MedicineFileUtil {

    private static final String UPLOAD_DIR = "uploads"; // Directory in the project root

    public void saveMedicine(Medicine medicine, MultipartFile image) throws IOException {
        List<Medicine> medicines = MedicineFileUtil.readMedicinesFromFile();
        int maxId = medicines.stream().mapToInt(Medicine::getId).max().orElse(0) + 1;
        medicine.setId(maxId);

        if (image != null && !image.isEmpty()) {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = "medicine_" + maxId + ".jpg";
            String uniqueFileName = generateUniqueFileName(fileName, uploadPath);
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(image.getInputStream(), filePath);

            medicine.setImageUrl("/" + UPLOAD_DIR + "/" + uniqueFileName);
        }

        medicines.add(medicine);
        MedicineFileUtil.writeMedicinesToFile(medicines);
    }

    public void updateMedicine(Medicine medicine, MultipartFile image) throws IOException {
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
    }

    public void deleteMedicine(int id) {
        List<Medicine> medicines = MedicineFileUtil.readMedicinesFromFile();
        medicines.removeIf(m -> m.getId() == id);
        MedicineFileUtil.writeMedicinesToFile(medicines);
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
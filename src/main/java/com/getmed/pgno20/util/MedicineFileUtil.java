package com.getmed.pgno20.util;

import com.getmed.pgno20.model.Medicine;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MedicineFileUtil {
    private static final String DIRECTORY_PATH = System.getProperty("user.dir") + File.separator + "data";
    private static final String FILE_PATH = DIRECTORY_PATH + File.separator + "medicines.txt";

    public static List<Medicine> readMedicinesFromFile() {
        List<Medicine> medicines = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            System.out.println("Medicines file does not exist: " + file.getAbsolutePath() + ". Returning empty list.");
            return medicines;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split on commas, but handle escaped commas
                String[] parts = splitLine(line);
                if (parts.length >= 5) {
                    Medicine medicine = new Medicine();
                    medicine.setId(Integer.parseInt(parts[0]));
                    medicine.setName(parts[1]);
                    medicine.setDescription(parts[2].replace("\\,", ",")); // Unescape commas in description
                    medicine.setPrice(Double.parseDouble(parts[3]));
                    medicine.setImageUrl(parts[4].isEmpty() ? null : parts[4]);
                    medicines.add(medicine);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading medicines from file " + file.getAbsolutePath() + ": " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing medicine data from file " + file.getAbsolutePath() + ": " + e.getMessage());
        }
        return medicines;
    }

    public static void writeMedicinesToFile(List<Medicine> medicines) {
        File dir = new File(DIRECTORY_PATH);
        if (!dir.exists() && !dir.mkdirs()) {
            System.err.println("Failed to create directory: " + dir.getAbsolutePath());
            throw new RuntimeException("Failed to create directory: " + dir.getAbsolutePath());
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Medicine medicine : medicines) {
                String imageUrl = medicine.getImageUrl() != null ? medicine.getImageUrl() : "";
                // Escape commas in description
                String escapedDescription = medicine.getDescription() != null ? medicine.getDescription().replace(",", "\\,") : "";
                String line = medicine.getId() + "," + medicine.getName() + "," +
                        escapedDescription + "," + medicine.getPrice() + "," + imageUrl;
                bw.write(line);
                bw.newLine();
                System.out.println("Written to file: " + line);
            }
            System.out.println("Successfully wrote " + medicines.size() + " medicines to " + FILE_PATH);
        } catch (IOException e) {
            System.err.println("Error writing medicines to file " + FILE_PATH + ": " + e.getMessage());
            throw new RuntimeException("Error writing medicines to file " + FILE_PATH + ": " + e.getMessage(), e);
        }
    }

    // Custom split method to handle escaped commas
    private static String[] splitLine(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean escaped = false;

        for (char c : line.toCharArray()) {
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == ',' && !escaped) {
                parts.add(field.toString());
                field = new StringBuilder();
            } else {
                field.append(c);
            }
            escaped = false;
        }
        parts.add(field.toString()); // Add the last field
        return parts.toArray(new String[0]);
    }
}
package com.getmed.pgno20.repository;

import com.getmed.pgno20.model.Admin;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class FileBasedAdminRepository implements AdminRepository {

    private static final String ADMIN_FILE_PATH = "src/main/resources/data/admins.txt";
    private final List<Admin> admins;

    public FileBasedAdminRepository() {
        this.admins = loadAdminsFromFile();
    }

    @Override
    public Admin findByEmail(String email) {
        return admins.stream()
                .filter(admin -> admin.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void save(Admin admin) {
        // Check if admin already exists (for update)
        Optional<Admin> existingAdmin = admins.stream()
                .filter(a -> a.getId() == admin.getId())
                .findFirst();

        if (existingAdmin.isPresent()) {
            // Update existing admin
            Admin adminToUpdate = existingAdmin.get();
            adminToUpdate.setName(admin.getName());
            adminToUpdate.setAddress(admin.getAddress());
            adminToUpdate.setContactNo(admin.getContactNo());
            adminToUpdate.setEmail(admin.getEmail());
            adminToUpdate.setPassword(admin.getPassword());
        } else {
            // Add new admin with a new ID
            int newId = admins.isEmpty() ? 1 : admins.stream().mapToInt(Admin::getId).max().getAsInt() + 1;
            admin.setId(newId);
            admins.add(admin);
        }

        // Save changes to file
        saveAdminsToFile();
    }

    @Override
    public void deleteById(int id) {
        admins.removeIf(admin -> admin.getId() == id);
        saveAdminsToFile();
    }

    @Override
    public List<Admin> findAll() {
        return new ArrayList<>(admins);
    }

    // Helper method to load admins from file
    private List<Admin> loadAdminsFromFile() {
        List<Admin> loadedAdmins = new ArrayList<>();
        File file = new File(ADMIN_FILE_PATH);
        if (!file.exists()) {
            File directory = file.getParentFile();
            if (!directory.exists() && !directory.mkdirs()) {
                System.err.println("Failed to create directory: " + directory.getAbsolutePath());
            }
            // Default admin creation
            Admin defaultAdmin = new Admin(1, "Admin Name", "123/A, Paratta Road, Keselwatta, Panadura", "+94 (38) 229-8759", "info@getmed.com", "admin123");
            loadedAdmins.add(defaultAdmin);
            saveAdminsToFile(loadedAdmins);
            return loadedAdmins;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 6) {
                    Admin admin = new Admin(
                            Integer.parseInt(data[0]),
                            data[1],
                            data[2],
                            data[3],
                            data[4],
                            data[5]
                    );
                    loadedAdmins.add(admin);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading admin data: " + e.getMessage());
        }

        return loadedAdmins;
    }

    // Helper method to save admins to file
    private void saveAdminsToFile() {
        saveAdminsToFile(this.admins);
    }

    private void saveAdminsToFile(List<Admin> adminsToSave) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ADMIN_FILE_PATH))) {
            for (Admin admin : adminsToSave) {
                String line = String.format("%d,%s,%s,%s,%s,%s",
                        admin.getId(),
                        admin.getName(),
                        admin.getAddress(),
                        admin.getContactNo(),
                        admin.getEmail(),
                        admin.getPassword()
                );
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing admin data: " + e.getMessage());
        }
    }
}
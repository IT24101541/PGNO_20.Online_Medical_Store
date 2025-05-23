package com.getmed.pgno20.service;

import com.getmed.pgno20.model.Admin;
import com.getmed.pgno20.model.User;
import com.getmed.pgno20.repository.FileBasedAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final FileBasedAdminRepository adminRepository;

    @Autowired
    public AdminService(FileBasedAdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public Admin getAdminByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    public void updateAdmin(Admin admin) {
        adminRepository.save(admin);
    }

    public Admin getAdminById(int id) {
        return adminRepository.findAll().stream()
                .filter(admin -> admin.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public boolean deleteAdmin(int id) {
        try {
            adminRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting admin: " + e.getMessage());
            return false;
        }
    }

    public void saveUser(User user) {
        Admin admin = new Admin(
                0, // Repository will auto-assign ID
                user.getName(),
                user.getAddress(),
                user.getContactNo(),
                user.getEmail(),
                user.getPassword()
        );
        adminRepository.save(admin);
    }
}
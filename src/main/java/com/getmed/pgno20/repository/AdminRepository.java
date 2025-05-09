package com.getmed.pgno20.repository;

import com.getmed.pgno20.model.Admin;
import java.util.List;

public interface AdminRepository {
    Admin findByEmail(String email);
    void save(Admin admin);
    void deleteById(int id);
    List<Admin> findAll();
}

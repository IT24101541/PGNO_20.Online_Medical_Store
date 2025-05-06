
package com.getmed.pgno20.controller;

import com.getmed.pgno20.model.Admin;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // Dummy admin data for demonstration purposes
    private Admin dummyAdmin = new Admin(1, "Admin Name", "123/A, Paratta Road, Keselwatta, Panadura", "+94 (38) 229-8759", "info@getmed.com", "password");

    // View Admin Details
    @GetMapping("/view")
    public String viewAdmin(Model model) {
        if (dummyAdmin == null) {
            model.addAttribute("message", "No admin details available.");
            return "delete_admin";
        }
        // Add admin details to the model
        model.addAttribute("admin", dummyAdmin);
        return "view_admin"; // This should match view_admin.html in templates
    }

    // Show Update Admin Form
    @GetMapping("/update")
    public String updateAdminForm(Model model) {
        if (dummyAdmin == null) {
            model.addAttribute("message", "No admin details available.");
            return "delete_admin";
        }
        // Add admin details to the model
        model.addAttribute("admin", dummyAdmin);
        return "update_admin"; // This should match update_admin.html in templates
    }

    // Update Admin Details
    @PostMapping("/update")
    public String updateAdmin(@ModelAttribute Admin admin, Model model) {
        // Update the dummy admin details with the provided data
        dummyAdmin.setName(admin.getName());
        dummyAdmin.setAddress(admin.getAddress());
        dummyAdmin.setContactNo(admin.getContactNo());
        dummyAdmin.setEmail(admin.getEmail());
        dummyAdmin.setPassword(admin.getPassword());

        // Add updated admin details to the model
        model.addAttribute("admin", dummyAdmin);
        return "redirect:/admin/view"; // Redirect to view_admin.html to see updated details
    }

    // Show Delete Admin Confirmation
    @GetMapping("/delete")
    public String confirmDelete(Model model) {
        if (dummyAdmin == null) {
            model.addAttribute("message", "No admin details available.");
            return "delete_admin";
        }
        model.addAttribute("admin", dummyAdmin);
        return "delete_admin"; // Confirmation page
    }

    // Delete Admin Details
    @PostMapping("/delete")
    public String deleteAdmin(Model model) {
        dummyAdmin = null;
        model.addAttribute("message", "Admin details deleted successfully.");
        return "delete_admin";
    }
}

// Admin.java
package com.getmed.pgno20.model;

public class Admin {
    private String name;
    private String address;
    private String contactNo;
    private String email;
    private String password;

    public Admin(String name, String address, String contactNo, String email, String password) {
        this.name = name;
        this.address = address;
        this.contactNo = contactNo;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNo() {  return contactNo;   }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

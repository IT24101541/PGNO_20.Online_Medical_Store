package com.getmed.pgno20.model;

public class Admin {
    private int id;
    private String name;
    private String address;
    private String contactNo;
    private String email;
    private String password;

    // Default constructor (required for Spring form binding)
    public Admin() {
    }

    // Parameterized constructor
    public Admin(int id, String name, String address, String contactNo, String email, String password) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.contactNo = contactNo;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getContactNo() {
        return contactNo;
    }

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

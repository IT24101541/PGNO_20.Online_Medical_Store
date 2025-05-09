package com.getmed.pgno20.model;

public class Customer {
    private int id;
    private String name;
    private String address;
    private String contactNo;
    private String email;
    private String password;

    public Customer() {}

    // 6-field constructor (id included)
    public Customer(int id, String name, String address, String contactNo, String email, String password) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.contactNo = contactNo;
        this.email = email;
        this.password = password;
    }

    // 5-field constructor (for repository compatibility, generates id)
    public Customer(String name, String address, String contactNo, String email, String password) {
        this.id = generateUniqueId(); // Placeholder for unique id generation
        this.name = name;
        this.address = address;
        this.contactNo = contactNo;
        this.email = email;
        this.password = password;
    }

    // Placeholder method to generate a unique id (e.g., based on list size or timestamp)
    private int generateUniqueId() {
        // In a real application, use a database sequence or increment based on existing IDs
        return (int) (System.currentTimeMillis() % 1000000); // Simple unique id for demo
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
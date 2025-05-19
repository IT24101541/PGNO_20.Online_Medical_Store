package com.getmed.pgno20.model;

public class Admin extends User {
    private int id;

    // Constructors
    public Admin() {
        super();
        setRole("admin"); // Set role in constructor
    }

    public Admin(int id, String name, String address, String contactNo, String email, String password) {
        super(name, address, contactNo, email, password, "admin");
        this.id = id;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
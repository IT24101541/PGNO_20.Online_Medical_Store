package com.getmed.pgno20.model;

public class Customer extends User {
    private int id;

    // Constructors
    public Customer() {
        super();
        setRole("customer"); // Set role in constructor
    }

    public Customer(int id, String name, String address, String contactNo, String email, String password) {
        super(name, address, contactNo, email, password, "customer");
        this.id = id;
    }

    public Customer(String name, String address, String contactNo, String email, String password) {
        super(name, address, contactNo, email, password, "customer");
        this.id = generateUniqueId();
    }

    // Placeholder method to generate a unique id
    private int generateUniqueId() {
        return (int) (System.currentTimeMillis() % 1000000);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
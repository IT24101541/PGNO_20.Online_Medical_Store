package com.getmed.pgno20.model;

public class Order {
    private int id;
    private String customerName;
    private String medicineName;
    private int quantity;
    private String orderDate;

    public Order() {}

    public Order(int id, String customerName, String medicineName, int quantity, String orderDate) {
        this.id = id;
        this.customerName = customerName;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.orderDate = orderDate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
}

package com.getmed.pgno20.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order {
    private int id;
    private String customerEmail;
    private Map<String, Integer> medicineQuantities = new HashMap<>();
    private int totalQuantity;
    private String orderDate;
    private String status;
    private String prescriptionUrl;
    private String receiptUrl;
    private String medicineNamesString; // New field for comma-separated medicine names

    // Constructor
    public Order() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public Map<String, Integer> getMedicineQuantities() {
        return medicineQuantities;
    }

    public void setMedicineQuantities(Map<String, Integer> medicineQuantities) {
        this.medicineQuantities = medicineQuantities;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrescriptionUrl() {
        return prescriptionUrl;
    }

    public void setPrescriptionUrl(String prescriptionUrl) {
        this.prescriptionUrl = prescriptionUrl;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    public List<String> getMedicineNames() {
        return new ArrayList<>(medicineQuantities.keySet());
    }

    // New getter and setter for medicineNamesString
    public String getMedicineNamesString() {
        return medicineNamesString;
    }

    public void setMedicineNamesString(String medicineNamesString) {
        this.medicineNamesString = medicineNamesString;
    }
}
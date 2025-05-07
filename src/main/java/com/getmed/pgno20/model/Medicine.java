package com.getmed.pgno20.model;

import java.io.Serializable;
import java.util.Objects;

public class Medicine implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String description;
    private double price;
    private String imageUrl; // Optional field for an image path

    public Medicine() {}

    public Medicine(int id, String name, String description, double price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medicine medicine = (Medicine) o;
        return id == medicine.id &&
                Double.compare(medicine.price, price) == 0 &&
                Objects.equals(name, medicine.name) &&
                Objects.equals(description, medicine.description) &&
                Objects.equals(imageUrl, medicine.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, price, imageUrl);
    }
}
package com.example.myfirstapplication;

import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private double price;
    private String description;
    private int imageResId;


    public Product() {
    }

    public Product(String name, double price, String description, int imageResId) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }

    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}
package com.example.myfirstapplication;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private String orderId;
    private String username;
    private String orderDate;
    private double totalPrice;
    private List<CartItem> items;
    private String status;

    public Order() {
    }

    public Order(String orderId, String username, String orderDate, double totalPrice, List<CartItem> items, String status) {
        this.orderId = orderId;
        this.username = username;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.items = items;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

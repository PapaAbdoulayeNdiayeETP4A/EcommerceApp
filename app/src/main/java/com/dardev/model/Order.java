package com.dardev.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Order {
    @SerializedName("id")
    private int id;

    @SerializedName("userId")
    private int userId;

    @SerializedName("shippingId")
    private int shippingId;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("total_price")
    private double totalPrice;

    @SerializedName("status")
    private String status;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("items")
    private List<OrderItem> items;

    public Order() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getShippingId() {
        return shippingId;
    }

    public void setShippingId(int shippingId) {
        this.shippingId = shippingId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
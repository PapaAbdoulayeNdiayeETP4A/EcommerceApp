package com.dardev.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Ordering {
    @SerializedName("userId")
    private int userId;

    @SerializedName("shippingId")
    private int shippingId;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("totalPrice")
    private double totalPrice;

    @SerializedName("products")
    private List<OrderItem> products;

    public Ordering() {
    }

    public Ordering(int userId, int shippingId, String paymentMethod, double totalPrice, List<OrderItem> products) {
        this.userId = userId;
        this.shippingId = shippingId;
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
        this.products = products;
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

    public List<OrderItem> getProducts() {
        return products;
    }

    public void setProducts(List<OrderItem> products) {
        this.products = products;
    }
}
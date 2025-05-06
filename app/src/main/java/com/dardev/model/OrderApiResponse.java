package com.dardev.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderApiResponse {
    @SerializedName("orders")
    private List<Order> orders;

    public OrderApiResponse() {
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
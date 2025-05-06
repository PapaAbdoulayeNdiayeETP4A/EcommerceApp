package com.dardev.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HistoryApiResponse {
    @SerializedName("history")
    private List<Product> history;

    public HistoryApiResponse() {
    }

    public List<Product> getProductsInHistory() {
        return history;
    }

    public void setProductsInHistory(List<Product> history) {
        this.history = history;
    }
}
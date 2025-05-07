package com.dardev.model;

import com.google.gson.annotations.SerializedName;

public class Favorite {
    @SerializedName("userId")
    private int userId;

    @SerializedName("productId")
    private int productId;

    public Favorite(int userId, int productId) {
        this.userId = userId;
        this.productId = productId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
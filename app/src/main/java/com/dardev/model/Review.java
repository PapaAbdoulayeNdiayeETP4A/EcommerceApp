package com.dardev.model;

import com.google.gson.annotations.SerializedName;

public class Review {
    @SerializedName("userId")
    private int userId;

    @SerializedName("productId")
    private int productId;

    @SerializedName("rating")
    private int rating;

    @SerializedName("review")
    private String review;

    public Review() {
    }

    public Review(int userId, int productId, int rating, String review) {
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.review = review;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
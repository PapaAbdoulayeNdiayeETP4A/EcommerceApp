package com.dardev.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewApiResponse {
    @SerializedName("reviews")
    private List<Review> reviews;

    public ReviewApiResponse() {
    }

    public List<Review> getAllReviews() {
        return reviews;
    }

    public void setAllReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
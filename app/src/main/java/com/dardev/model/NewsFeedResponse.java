package com.dardev.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsFeedResponse {
    @SerializedName("posters")
    private List<Poster> posters;

    public NewsFeedResponse() {
    }

    public List<Poster> getPosters() {
        return posters;
    }

    public void setPosters(List<Poster> posters) {
        this.posters = posters;
    }
}
package com.dardev.model;

import com.google.gson.annotations.SerializedName;

public class Shipping {
    @SerializedName("userId")
    private int userId;

    @SerializedName("name")
    private String name;

    @SerializedName("address")
    private String address;

    @SerializedName("city")
    private String city;

    @SerializedName("country")
    private String country;

    @SerializedName("postal_code")
    private String postalCode;

    @SerializedName("phone")
    private String phone;

    public Shipping() {
    }

    public Shipping(int userId, String name, String address, String city, String country, String postalCode, String phone) {
        this.userId = userId;
        this.name = name;
        this.address = address;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
        this.phone = phone;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
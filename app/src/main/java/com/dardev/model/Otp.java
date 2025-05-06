package com.dardev.model;

import com.google.gson.annotations.SerializedName;

public class Otp {
    @SerializedName("email")
    private String email;

    @SerializedName("otp")
    private String otp;

    public Otp() {
    }

    public Otp(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
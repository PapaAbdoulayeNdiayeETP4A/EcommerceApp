package com.dardev.model;

import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("type")
    private String type;

    public Notification() {
    }

    public Notification(int id, String title, String message, String createdAt, String type) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Formats the timestamp into a human-readable string like "2 hours ago", "Yesterday", etc.
     * @return A formatted time string
     */
    public String getFormattedTime() {
        // In a real implementation, you would parse the createdAt timestamp
        // and calculate the time difference from now

        // This is a placeholder implementation
        return "2 hours ago";
    }

    /**
     * Returns the appropriate icon resource ID based on notification type
     * @return The resource ID for the notification icon
     */
    public int getIconResource() {
        switch (type) {
            case "order":
                return android.R.drawable.ic_dialog_info;
            case "promotion":
                return android.R.drawable.ic_dialog_alert;
            case "payment":
                return android.R.drawable.ic_dialog_dialer;
            default:
                return android.R.drawable.ic_dialog_email;
        }
    }
}
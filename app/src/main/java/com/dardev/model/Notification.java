package com.dardev.model;

import com.google.gson.annotations.SerializedName;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.dardev.R;

public class Notification {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("type")
    private String type;

    @SerializedName("is_read")
    private boolean isRead;

    @SerializedName("data")
    private String data; // Pour les données supplémentaires (JSON)

    public Notification() {
    }

    public Notification(int id, String title, String message, String createdAt, String type) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.type = type;
        this.isRead = false;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    /**
     * Formats the timestamp into a human-readable string like "2 hours ago", "Yesterday", etc.
     * @return A formatted time string
     */
    public String getFormattedTime() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date notificationDate = dateFormat.parse(createdAt);
            Date now = new Date();

            if (notificationDate != null) {
                long timeDiff = now.getTime() - notificationDate.getTime();

                long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiff);
                long hours = TimeUnit.MILLISECONDS.toHours(timeDiff);
                long days = TimeUnit.MILLISECONDS.toDays(timeDiff);

                if (minutes < 1) {
                    return "À l'instant";
                } else if (minutes < 60) {
                    return minutes + " min";
                } else if (hours < 24) {
                    return hours + "h";
                } else if (days == 1) {
                    return "Hier";
                } else if (days < 7) {
                    return days + " jours";
                } else {
                    SimpleDateFormat shortFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
                    return shortFormat.format(notificationDate);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Récemment";
    }

    /**
     * Returns the appropriate icon resource ID based on notification type
     * @return The resource ID for the notification icon
     */
    public int getIconResource() {
        switch (type) {
            case "order":
                return R.drawable.shopping_bag;
            case "promotion":
                return R.drawable.ic_offer;
            case "payment":
                return R.drawable.ic_payment;
            case "shipping":
                return R.drawable.ic_delivery;
            case "account":
                return R.drawable.ic_account;
            default:
                return R.drawable.notifications;
        }
    }
}
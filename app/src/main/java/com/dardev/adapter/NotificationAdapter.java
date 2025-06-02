package com.dardev.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.dardev.R;
import com.dardev.model.Notification;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private List<Notification> notifications;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(int position);
    }

    public NotificationAdapter(Context context, List<Notification> notifications, OnNotificationClickListener listener) {
        this.context = context;
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        holder.titleTextView.setText(notification.getTitle());
        holder.descriptionTextView.setText(notification.getMessage());
        holder.timeTextView.setText(notification.getFormattedTime());
        holder.iconImageView.setImageResource(notification.getIconResource());

        // Style différent pour les notifications non lues
        if (!notification.isRead()) {
            holder.titleTextView.setTypeface(null, Typeface.BOLD);
            holder.itemView.setBackgroundResource(R.drawable.unread_notification_background);
        } else {
            holder.titleTextView.setTypeface(null, Typeface.NORMAL);
            holder.itemView.setBackgroundResource(R.drawable.read_notification_background);
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView iconImageView;
        public TextView titleTextView;
        public TextView descriptionTextView;
        public TextView timeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iconImageView = itemView.findViewById(R.id.notification_icon);
            titleTextView = itemView.findViewById(R.id.notification_title);
            descriptionTextView = itemView.findViewById(R.id.notification_description);
            timeTextView = itemView.findViewById(R.id.notification_time);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onNotificationClick(position);
                }
            }
        }
    }
}
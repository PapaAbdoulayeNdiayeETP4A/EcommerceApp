package com.dardev.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import com.dardev.R;
import com.dardev.adapter.NotificationAdapter;
import com.dardev.databinding.NotificationsBinding;
import com.dardev.model.Notification;

public class NotificationsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "NotificationsActivity";

    private NotificationsBinding binding;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notifications = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.notifications);

        // Set status bar color
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, getTheme()));

        // Setup toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Notifications");
        }

        // Setup swipe refresh
        binding.swipeRefresh.setOnRefreshListener(this);
        binding.swipeRefresh.setColorSchemeResources(
                R.color.green,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );

        // Setup recycler view
        recyclerView = binding.notificationsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup adapter
        adapter = new NotificationAdapter(this, notifications, position -> {
            // Handle notification click
            Notification notification = notifications.get(position);
            Toast.makeText(NotificationsActivity.this,
                    "Clicked: " + notification.getTitle(),
                    Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(adapter);

        // Load sample notifications for now
        loadSampleNotifications();
    }

    private void loadSampleNotifications() {
        // In a real app, you would fetch these from your API
        notifications.clear();

        // Add sample notifications
        notifications.add(new Notification(1, "Order Shipped",
                "Your order #12345 has been shipped!",
                "2023-05-10T14:30:00", "order"));

        notifications.add(new Notification(2, "50% OFF Sale!",
                "Don't miss our amazing 50% off sale on all shoes!",
                "2023-05-09T09:15:00", "promotion"));

        notifications.add(new Notification(3, "Payment Successful",
                "Your payment for order #12345 was successful.",
                "2023-05-08T18:45:00", "payment"));

        adapter.notifyDataSetChanged();

        // Show empty state if needed
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (notifications.isEmpty()) {
            binding.emptyNotificationsLayout.setVisibility(View.VISIBLE);
            binding.notificationsRecyclerView.setVisibility(View.GONE);
        } else {
            binding.emptyNotificationsLayout.setVisibility(View.GONE);
            binding.notificationsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        // In a real app, you would reload notifications from your API
        loadSampleNotifications();
        binding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
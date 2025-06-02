package com.dardev.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.dardev.model.NotificationApiResponse;
import com.dardev.net.Api;
import com.dardev.net.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "NotificationsActivity";

    private NotificationsBinding binding;
    private NotificationAdapter adapter;
    private List<Notification> notifications = new ArrayList<>();
    private Api api;
    private int userId;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasMorePages = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.notifications);

        // Initialiser l'API
        api = RetrofitClient.getInstance().getApi();

        // Récupérer l'ID utilisateur depuis SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Erreur: Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupUI();
        setupRecyclerView();
        loadNotifications(1);
    }

    private void setupUI() {
        // Configuration de la barre d'outils
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Notifications");
        }

        // Configuration du SwipeRefreshLayout
        binding.swipeRefresh.setOnRefreshListener(this);
        binding.swipeRefresh.setColorSchemeResources(
                R.color.green,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
    }

    private void setupRecyclerView() {
        binding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NotificationAdapter(this, notifications, new NotificationAdapter.OnNotificationClickListener() {
            @Override
            public void onNotificationClick(int position) {
                handleNotificationClick(position);
            }
        });

        binding.notificationsRecyclerView.setAdapter(adapter);

        // Pagination avec scroll listener
        binding.notificationsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading && hasMorePages) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        loadNotifications(currentPage + 1);
                    }
                }
            }
        });
    }

    private void loadNotifications(int page) {
        if (isLoading) return;

        isLoading = true;
        if (page == 1) {
            binding.swipeRefresh.setRefreshing(true);
        }

        Call<NotificationApiResponse> call = api.getNotifications(userId, page);
        call.enqueue(new Callback<NotificationApiResponse>() {
            @Override
            public void onResponse(Call<NotificationApiResponse> call, Response<NotificationApiResponse> response) {
                isLoading = false;
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    NotificationApiResponse apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        if (page == 1) {
                            notifications.clear();
                        }

                        notifications.addAll(apiResponse.getNotifications());
                        adapter.notifyDataSetChanged();

                        currentPage = page;
                        hasMorePages = page < apiResponse.getTotalPages();

                        updateEmptyState();
                    } else {
                        showError("Erreur: " + apiResponse.getMessage());
                    }
                } else {
                    showError("Erreur lors du chargement des notifications");
                }
            }

            @Override
            public void onFailure(Call<NotificationApiResponse> call, Throwable t) {
                isLoading = false;
                binding.swipeRefresh.setRefreshing(false);
                showError("Erreur de connexion: " + t.getMessage());
                Log.e(TAG, "Erreur API", t);
            }
        });
    }

    private void handleNotificationClick(int position) {
        Notification notification = notifications.get(position);

        // Marquer comme lue si elle ne l'est pas déjà
        if (!notification.isRead()) {
            markNotificationAsRead(notification.getId(), position);
        }

        // Gérer l'action selon le type de notification
        handleNotificationAction(notification);
    }

    private void markNotificationAsRead(int notificationId, int position) {
        Call<okhttp3.ResponseBody> call = api.markNotificationAsRead(notificationId);
        call.enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    notifications.get(position).setRead(true);
                    adapter.notifyItemChanged(position);
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                Log.e(TAG, "Erreur lors du marquage comme lu", t);
            }
        });
    }

    private void handleNotificationAction(Notification notification) {
        // Ici vous pouvez gérer les actions spécifiques selon le type de notification
        switch (notification.getType()) {
            case "order":
                // Ouvrir la page de commandes
                Toast.makeText(this, "Redirection vers les commandes", Toast.LENGTH_SHORT).show();
                break;
            case "promotion":
                // Ouvrir la page des promotions
                Toast.makeText(this, "Redirection vers les promotions", Toast.LENGTH_SHORT).show();
                break;
            case "payment":
                // Ouvrir la page des paiements
                Toast.makeText(this, "Redirection vers les paiements", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, notification.getTitle(), Toast.LENGTH_SHORT).show();
                break;
        }
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

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        hasMorePages = true;
        loadNotifications(1);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
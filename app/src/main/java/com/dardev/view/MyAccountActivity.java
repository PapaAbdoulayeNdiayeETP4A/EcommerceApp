package com.dardev.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.dardev.R;
import com.dardev.databinding.MyAccountBinding;

public class MyAccountActivity extends AppCompatActivity {
    private static final String TAG = "MyAccountActivity";

    private MyAccountBinding binding;
    private TextView nameTextView, emailTextView;
    private Button editProfileButton, changePasswordButton, myOrdersButton, myWishlistButton, addressesButton, logoutButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.my_account);

        // Set status bar color
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, getTheme()));

        // Setup toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("My Account");
        }

        // Initialize views
        nameTextView = binding.userName;
        emailTextView = binding.userEmail;
        editProfileButton = binding.editProfileButton;
        changePasswordButton = binding.changePasswordButton;
        myOrdersButton = binding.myOrdersButton;
        myWishlistButton = binding.myWishlistButton;
        addressesButton = binding.addressesButton;
        logoutButton = binding.logoutButton;

        // Load user data
        loadUserData();

        // Setup click listeners
        editProfileButton.setOnClickListener(v -> {
            // Navigate to edit profile screen
            Toast.makeText(MyAccountActivity.this, "Edit Profile feature coming soon", Toast.LENGTH_SHORT).show();
        });

        changePasswordButton.setOnClickListener(v -> {
            // Navigate to change password screen
            Toast.makeText(MyAccountActivity.this, "Change Password feature coming soon", Toast.LENGTH_SHORT).show();
        });

        myOrdersButton.setOnClickListener(v -> {
            // Navigate to orders screen
            Intent intent = new Intent(MyAccountActivity.this, MyOrdersActivity.class);
            startActivity(intent);
        });

        myWishlistButton.setOnClickListener(v -> {
            // Navigate to wishlist screen
            Intent intent = new Intent(MyAccountActivity.this, MyWishlistActivity.class);
            startActivity(intent);
        });

        addressesButton.setOnClickListener(v -> {
            // Navigate to addresses screen
            Toast.makeText(MyAccountActivity.this, "Addresses feature coming soon", Toast.LENGTH_SHORT).show();
        });

        logoutButton.setOnClickListener(v -> {
            // Logout user
            logout();
        });
    }

    private void loadUserData() {
        // In a real app, you would fetch user data from your database or API
        // For now, we'll use SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String email = prefs.getString("email", "");
        String name = prefs.getString("username", "User");

        nameTextView.setText(name);
        emailTextView.setText(email);
    }

    private void logout() {
        // Clear user data from SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences("user_prefs", MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        // Navigate to login screen
        Intent intent = new Intent(MyAccountActivity.this, SignInSignUp.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
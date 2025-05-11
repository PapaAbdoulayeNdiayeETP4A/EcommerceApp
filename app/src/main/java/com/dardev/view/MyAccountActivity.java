package com.dardev.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.dardev.R;
import com.dardev.ViewModel.UserViewModel;
import com.dardev.databinding.MyAccountBinding;
import com.dardev.storage.LoginUtils;

public class MyAccountActivity extends AppCompatActivity {
    private static final String TAG = "MyAccountActivity";

    private MyAccountBinding binding;
    private UserViewModel userViewModel;

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
            getSupportActionBar().setTitle("Mon Compte");
        }

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Vérifier si l'utilisateur est connecté
//        if (!LoginUtils.getInstance(this).isLoggedIn()) {
//            // Rediriger vers la page de connexion si non connecté
//            Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(this, SignInSignUp.class));
//            finish();
//            return;
//        }

        // Load user data
        loadUserDataFromApi();

        // Setup click listeners
        setupButtonListeners();
    }

    private void loadUserDataFromApi() {
        // Récupérer l'ID de l'utilisateur
        int userId = LoginUtils.getInstance(this).getUserId();
        Log.d(TAG, "Chargement des données utilisateur, ID: " + userId);

        // Charger les données utilisateur depuis l'API
        userViewModel.getUserDetails(userId).observe(this, user -> {
            if (user != null) {
                // Mettre à jour l'interface utilisateur avec les données
                binding.userName.setText(user.getUsername());
                binding.userEmail.setText(user.getEmail());
                Log.d(TAG, "Données utilisateur chargées avec succès");
            } else {
                Toast.makeText(this, "Erreur lors du chargement des données utilisateur", Toast.LENGTH_SHORT).show();
                // Charger les données depuis SharedPreferences en cas d'échec
                loadUserDataFromSharedPreferences();
            }
        });
    }

    private void loadUserDataFromSharedPreferences() {
        // Méthode de secours pour charger les données depuis SharedPreferences
        binding.userName.setText(LoginUtils.getInstance(this).getUserInfo().getUsername());
        binding.userEmail.setText(LoginUtils.getInstance(this).getUserInfo().getEmail());
    }

    private void setupButtonListeners() {
        binding.editProfileButton.setOnClickListener(v -> {
            // Implémentation à venir
            Toast.makeText(MyAccountActivity.this, "Modification du profil bientôt disponible", Toast.LENGTH_SHORT).show();
        });

        binding.changePasswordButton.setOnClickListener(v -> {
            // Implémentation à venir
            Toast.makeText(MyAccountActivity.this, "Changement de mot de passe bientôt disponible", Toast.LENGTH_SHORT).show();
        });

        binding.myOrdersButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyAccountActivity.this, MyOrdersActivity.class);
            startActivity(intent);
        });

        binding.myWishlistButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyAccountActivity.this, MyWishlistActivity.class);
            startActivity(intent);
        });

        binding.addressesButton.setOnClickListener(v -> {
            // Implémentation à venir
            Toast.makeText(MyAccountActivity.this, "Gestion des adresses bientôt disponible", Toast.LENGTH_SHORT).show();
        });

        binding.logoutButton.setOnClickListener(v -> {
            logout();
        });
    }

    private void logout() {
        // Déconnecter l'utilisateur
        LoginUtils.getInstance(this).logout();
        Toast.makeText(this, "Vous avez été déconnecté", Toast.LENGTH_SHORT).show();

        // Naviguer vers l'écran de connexion
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
package com.dardev.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.dardev.R;
import com.dardev.ViewModel.UserViewModel;
import com.dardev.storage.LoginUtils;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = "UserActivity";

    private TextView usernameTextView, emailTextView;
    private Button myAccountButton, loginButton, myOrdersButton, myWishlistButton, addressesButton, logoutButton;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Initialiser le ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Initialiser les vues
        usernameTextView = findViewById(R.id.user_name);
        emailTextView = findViewById(R.id.user_email);
        myAccountButton = findViewById(R.id.edit_profile_button);
        loginButton = findViewById(R.id.logout_button);
        myOrdersButton = findViewById(R.id.my_orders_button);
        myWishlistButton = findViewById(R.id.my_wishlist_button);
        addressesButton = findViewById(R.id.addresses_button);
        logoutButton = findViewById(R.id.logout_button);

        // Vérifier si l'utilisateur est connecté
//        if (LoginUtils.getInstance(this).isLoggedIn()) {
            // Charger les données utilisateur depuis l'API
            loadUserDataFromApi();

            // Configuration des boutons
            setupLoggedInButtonListeners();

            // Changer le texte du bouton logout
            logoutButton.setText("Se déconnecter");
//        } else {
//            // Afficher l'état non connecté
//            setupNotLoggedInState();
//        }
    }

    private void loadUserDataFromApi() {
        int userId = LoginUtils.getInstance(this).getUserId();
        Log.d(TAG, "Chargement des données utilisateur, ID: " + userId);

        // Observer pour obtenir les détails utilisateur
        userViewModel.getUserDetails(userId).observe(this, user -> {
            if (user != null) {
                // Mettre à jour l'interface avec les données de l'API
                usernameTextView.setText(user.getUsername());
                emailTextView.setText(user.getEmail());
                Log.d(TAG, "Données utilisateur chargées avec succès");
            } else {
                // Fallback: utiliser les données du SharedPreferences
                Log.d(TAG, "Échec du chargement depuis l'API, utilisation des données locales");
                loadUserDataFromSharedPreferences();
            }
        });
    }

    private void loadUserDataFromSharedPreferences() {
        usernameTextView.setText(LoginUtils.getInstance(this).getUserInfo().getUsername());
        emailTextView.setText(LoginUtils.getInstance(this).getUserInfo().getEmail());
    }

    private void setupLoggedInButtonListeners() {
        // Afficher tous les boutons
        myAccountButton.setVisibility(View.VISIBLE);
        myOrdersButton.setVisibility(View.VISIBLE);
        myWishlistButton.setVisibility(View.VISIBLE);
        addressesButton.setVisibility(View.VISIBLE);

        // Configurer le bouton "Mon compte"
        myAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyAccountActivity.class);
            startActivity(intent);
        });

        // Configurer les autres boutons
        myOrdersButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyOrdersActivity.class);
            startActivity(intent);
        });

        myWishlistButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyWishlistActivity.class);
            startActivity(intent);
        });

        addressesButton.setOnClickListener(v -> {
            Toast.makeText(this, "Gestion des adresses bientôt disponible",
                    Toast.LENGTH_SHORT).show();
        });

        // Configurer le bouton de déconnexion
        logoutButton.setOnClickListener(v -> {
            LoginUtils.getInstance(this).logout();
            Toast.makeText(this, "Vous avez été déconnecté", Toast.LENGTH_SHORT).show();

            // Recharger la vue
            setupNotLoggedInState();
        });
    }

    private void setupNotLoggedInState() {
        // Cacher les infos utilisateur et les boutons spécifiques
        usernameTextView.setText("Non connecté");
        emailTextView.setVisibility(View.GONE);
        myAccountButton.setVisibility(View.GONE);
        myOrdersButton.setVisibility(View.GONE);
        myWishlistButton.setVisibility(View.GONE);
        addressesButton.setVisibility(View.GONE);

        // Afficher et configurer le bouton de connexion
        loginButton.setVisibility(View.VISIBLE);
        loginButton.setText("Se connecter");

        // Configurer le bouton de connexion
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignInSignUp.class);
            startActivity(intent);
        });
    }
}
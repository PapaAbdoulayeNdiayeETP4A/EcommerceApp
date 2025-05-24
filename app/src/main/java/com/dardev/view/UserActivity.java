package com.dardev.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.dardev.R;
import com.dardev.ViewModel.UserViewModel;
import com.dardev.model.User;
import com.dardev.storage.LoginUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = "UserActivity";

    private TextView usernameTextView, emailTextView;
    private Button myAccountButton, changePasswordButton, loginButton, myOrdersButton, myWishlistButton, addressesButton, logoutButton;
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
        changePasswordButton = findViewById(R.id.change_password_button);
        loginButton = findViewById(R.id.logout_button);
        myOrdersButton = findViewById(R.id.my_orders_button);
        myWishlistButton = findViewById(R.id.my_wishlist_button);
        addressesButton = findViewById(R.id.addresses_button);
        logoutButton = findViewById(R.id.logout_button);

        // Vérifier si l'utilisateur est connecté
        if (LoginUtils.getInstance(this).isLoggedIn()) {
            // Charger les données utilisateur depuis l'API
            loadUserDataFromApi();

            // Configuration des boutons
            setupLoggedInButtonListeners();

            // Changer le texte du bouton logout
            logoutButton.setText("Se déconnecter");
        } else {
            // Afficher l'état non connecté
            setupNotLoggedInState();
        }
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
        changePasswordButton.setVisibility(View.VISIBLE);
        myOrdersButton.setVisibility(View.VISIBLE);
        myWishlistButton.setVisibility(View.VISIBLE);
        addressesButton.setVisibility(View.VISIBLE);

        // Configurer le bouton "Modifier profil"
        myAccountButton.setOnClickListener(v -> {
            showEditProfileDialog();
        });

        // Configurer le bouton "Changer mot de passe"
        changePasswordButton.setOnClickListener(v -> {
            showChangePasswordDialog();
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

    private void showEditProfileDialog() {
        // Créer un dialog bottom sheet
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        dialog.setContentView(dialogView);

        // Initialiser les champs du formulaire
        TextInputEditText usernameEditText = dialogView.findViewById(R.id.edit_username);
        TextInputEditText emailEditText = dialogView.findViewById(R.id.edit_email);
        Button saveButton = dialogView.findViewById(R.id.save_profile_button);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        ProgressBar loadingIndicator = dialogView.findViewById(R.id.loading_indicator);

        // Remplir les champs avec les informations actuelles
        usernameEditText.setText(usernameTextView.getText().toString());
        emailEditText.setText(emailTextView.getText().toString());

        // Gérer les clics sur les boutons
        saveButton.setOnClickListener(v -> {
            // Récupérer les nouvelles valeurs
            String newUsername = usernameEditText.getText().toString().trim();
            String newEmail = emailEditText.getText().toString().trim();

            // Validation des champs
            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Tous les champs sont requis", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validation basique de l'email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                Toast.makeText(this, "Veuillez entrer une adresse email valide", Toast.LENGTH_SHORT).show();
                return;
            }

            // Afficher l'indicateur de chargement
            if (loadingIndicator != null) {
                loadingIndicator.setVisibility(View.VISIBLE);
            }
            saveButton.setEnabled(false);

            // Récupérer l'ID utilisateur
            int userId = LoginUtils.getInstance(this).getUserId();
            Log.d(TAG, "Mise à jour du profil pour l'utilisateur ID: " + userId);

            // Appel à l'API via ViewModel
            userViewModel.updateProfile(newUsername, newEmail, userId).observe(this, response -> {
                // Masquer l'indicateur de chargement
                if (loadingIndicator != null) {
                    loadingIndicator.setVisibility(View.GONE);
                }
                saveButton.setEnabled(true);

                if (response != null) {
                    // Succès
                    // Mettre à jour l'interface utilisateur
                    usernameTextView.setText(newUsername);
                    emailTextView.setText(newEmail);

                    // Mettre à jour les informations locales
                    User currentUser = LoginUtils.getInstance(this).getUserInfo();
                    currentUser.setUsername(newUsername);
                    currentUser.setEmail(newEmail);
                    LoginUtils.getInstance(this).saveUserInfo(currentUser.getId(), currentUser.getUsername(), currentUser.getEmail());

                    Toast.makeText(this, "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    // Erreur
                    Toast.makeText(this, "Erreur lors de la mise à jour du profil", Toast.LENGTH_SHORT).show();
                }
            });
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showChangePasswordDialog() {
        // Créer un dialog bottom sheet
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        dialog.setContentView(dialogView);

        // Initialiser les champs du formulaire
        TextInputEditText currentPasswordEditText = dialogView.findViewById(R.id.current_password);
        TextInputEditText newPasswordEditText = dialogView.findViewById(R.id.new_password);
        TextInputEditText confirmPasswordEditText = dialogView.findViewById(R.id.confirm_password);
        Button saveButton = dialogView.findViewById(R.id.save_password_button);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);

        // Gérer les clics sur les boutons
        saveButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Tous les champs sont requis", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Les nouveaux mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                return;
            }

            // Dans une vraie implémentation, vous vérifieriez le mot de passe actuel et mettriez à jour le nouveau
            int userId = LoginUtils.getInstance(this).getUserId();

            userViewModel.updatePassword(newPassword, userId).observe(this, response -> {
                if (response != null) {
                    Toast.makeText(this, "Mot de passe mis à jour avec succès", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Erreur lors de la mise à jour du mot de passe", Toast.LENGTH_SHORT).show();
                }
            });
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void setupNotLoggedInState() {
        // Cacher les infos utilisateur et les boutons spécifiques
        usernameTextView.setText("Non connecté");
        emailTextView.setVisibility(View.GONE);
        myAccountButton.setVisibility(View.GONE);
        changePasswordButton.setVisibility(View.GONE);
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
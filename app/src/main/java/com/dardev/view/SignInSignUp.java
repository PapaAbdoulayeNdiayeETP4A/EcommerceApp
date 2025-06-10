package com.dardev.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.dardev.R;
import com.dardev.ViewModel.SignInSignUpViewModel;
import com.dardev.databinding.SigninSignupBinding;
import com.dardev.model.LoginApiResponse;
import com.dardev.model.RegisterApiResponse;
import com.dardev.model.User;
import com.google.android.material.textfield.TextInputEditText;

public class SignInSignUp extends AppCompatActivity {
    private static final String TAG = "SignInSignupActivity";

    ConstraintLayout signin_page, signup_page;
    Button continue_btn, signup_button;
    TextView signin, signup;
    TextInputEditText emailInput, passwordInput, nameInput, emailInput2, passwordInput2, reenterPassword;

    SigninSignupBinding signinSignupBinding;
    SignInSignUpViewModel signInSignUpViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Initializing sign in/sign up activity");

        // Utiliser DataBinding pour initialiser la vue
        signinSignupBinding = DataBindingUtil.setContentView(this, R.layout.signin_signup);

        // Initialiser les éléments d'interface
        signin_page = signinSignupBinding.signinPage;
        signup_page = signinSignupBinding.signupPage;
        continue_btn = signinSignupBinding.continueButton;
        signup_button = signinSignupBinding.signupButton;
        signin = signinSignupBinding.signin;
        signup = signinSignupBinding.signup;

        // Initialiser les champs de texte
        emailInput = signinSignupBinding.email;
        passwordInput = signinSignupBinding.password;
        nameInput = signinSignupBinding.name;
        emailInput2 = signinSignupBinding.email2;
        passwordInput2 = signinSignupBinding.password2;
        reenterPassword = signinSignupBinding.reenterPassword;

        // Configurer la couleur de la barre d'état
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, getTheme()));

        // Initialiser le ViewModel
        signInSignUpViewModel = new ViewModelProvider(this).get(SignInSignUpViewModel.class);

        // Observer les réponses de connexion
        signInSignUpViewModel.getLoginResponseLiveData().observe(this, new Observer<LoginApiResponse>() {
            @Override
            public void onChanged(LoginApiResponse response) {
                if (response != null) {
                    Log.d(TAG, "Received login response");

                    if (response.isSuccess()) {
                        saveUserInfo(response.getUserId(), response.getEmail());
                        startMainActivity();
                    } else {
                        String serverMessage = response.getMessage();
                        String errorMessage;

                        if (serverMessage != null && serverMessage.toLowerCase().contains("unauthorized")) {
                            errorMessage = "Email ou mot de passe incorrect.";
                        } else if (serverMessage != null) {
                            errorMessage = serverMessage;
                        } else {
                            errorMessage = "Échec de la connexion. Veuillez réessayer.";
                        }

                        Toast.makeText(SignInSignUp.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Réponse nulle (pas de connexion, erreur serveur)
                    Toast.makeText(SignInSignUp.this,
                            "Impossible de se connecter. Vérifiez votre connexion Internet.",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        // Observer les réponses d'inscription
        signInSignUpViewModel.getRegisterResponseLiveData().observe(this, response -> {
            if (response != null) {
                Log.d(TAG, "Received register response");

                if (response.isSuccess()) {
                    saveUserInfo(response.getUserId(), response.getEmail());
                    startMainActivity();
                } else {
                    String serverMessage = response.getMessage();
                    String errorMessage;

                    if (serverMessage != null && serverMessage.toLowerCase().contains("email already exists")) {
                        errorMessage = "Cette adresse email est déjà utilisée.";
                    } else if (serverMessage != null && serverMessage.toLowerCase().contains("invalid email")) {
                        errorMessage = "Adresse email invalide.";
                    } else if (serverMessage != null) {
                        errorMessage = serverMessage;
                    } else {
                        errorMessage = "Échec de l'inscription. Veuillez réessayer.";
                    }

                    Toast.makeText(SignInSignUp.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(SignInSignUp.this,
                        "Impossible de s’inscrire. Vérifiez votre connexion Internet.",
                        Toast.LENGTH_LONG).show();
            }
        });


        // Bouton de connexion
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Continue button clicked - attempting login");

                // Récupérer les valeurs des champs
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                // Valider les entrées
                if (validateLoginInput(email, password)) {
                    // Tentative de connexion
                    signInSignUpViewModel.loginUser(email, password);
                }
            }
        });

        // Bouton d'inscription
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Signup button clicked - attempting registration");

                // Récupérer les valeurs des champs
                String name = nameInput.getText().toString().trim();
                String email = emailInput2.getText().toString().trim();
                String password = passwordInput2.getText().toString().trim();
                String confirmPassword = reenterPassword.getText().toString().trim(); // Nouveau champ

                // Valider les entrées
                if (validateRegisterInput(name, email, password, confirmPassword)) {
                    // Tentative d'inscription
                    User user = new User(name, email, password);
                    signInSignUpViewModel.registerUser(user);
                }
            }
        });


        // Basculer vers la page de connexion
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Switching to signin page");
                signup_page.setVisibility(View.GONE);
                signin_page.setVisibility(View.VISIBLE);
            }
        });

        // Basculer vers la page d'inscription
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Switching to signup page");
                signin_page.setVisibility(View.GONE);
                signup_page.setVisibility(View.VISIBLE);
            }
        });
    }

    // Valider les entrées de connexion
    private boolean validateLoginInput(String email, String password) {
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Valider les entrées d'inscription
    private boolean validateRegisterInput(String name, String email, String password, String confirmPassword) {
        if (name.isEmpty()) {
            Toast.makeText(this, "Le nom est requis", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "L'adresse email est requise", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Adresse email invalide", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Le mot de passe doit contenir au moins 6 caractères", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    // Sauvegarder les informations utilisateur
    private void saveUserInfo(int userId, String email) {
        getSharedPreferences("user_prefs", MODE_PRIVATE)
                .edit()
                .putInt("user_id", userId)
                .putString("email", email)
                .putBoolean("is_logged_in", true)
                .apply();

        Log.d(TAG, "User info saved. ID: " + userId + ", Email: " + email);
    }

    // Démarrer l'activité principale
    private void startMainActivity() {
        Intent intent = new Intent(SignInSignUp.this, MainActivity.class);
        startActivity(intent);
        finish(); // Fermer cette activité
    }
}
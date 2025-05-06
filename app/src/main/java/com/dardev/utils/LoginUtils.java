package com.dardev.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.dardev.model.User;

/**
 * Classe utilitaire pour gérer les informations d'authentification et de session
 */
public class LoginUtils {
    private static final String SHARED_PREF_NAME = "user_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private static LoginUtils instance;
    private SharedPreferences sharedPreferences;

    private LoginUtils(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized LoginUtils getInstance(Context context) {
        if (instance == null) {
            instance = new LoginUtils(context);
        }
        return instance;
    }

    /**
     * Sauvegarde les informations utilisateur dans les SharedPreferences
     */
    public void saveUserInfo(int userId, String username, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    /**
     * Vérifie si l'utilisateur est connecté
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Récupère les informations utilisateur
     */
    public User getUserInfo() {
        User user = new User(
                sharedPreferences.getString(KEY_USERNAME, ""),
                sharedPreferences.getString(KEY_EMAIL, ""),
                ""
        );
        // Ici nous ne stockons pas le mot de passe dans les SharedPreferences pour des raisons de sécurité
        return user;
    }

    /**
     * Récupère l'ID de l'utilisateur connecté
     */
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    /**
     * Déconnecte l'utilisateur
     */
    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
package com.dardev.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.dardev.R;
import com.dardev.ViewModel.AddFavoriteViewModel;
import com.dardev.ViewModel.RemoveFavoriteViewModel;
import com.dardev.model.Favorite;
import com.dardev.model.Product;
import com.dardev.utils.LoginUtils;
import com.dardev.utils.RequestCallback;

public class ShowProduct extends AppCompatActivity {
    private static final String TAG = "ShowProduct";

    Button buy_now;
    ImageButton favoriteButton;
    TextView title, price;

    private Product currentProduct;
    private AddFavoriteViewModel addFavoriteViewModel;
    private RemoveFavoriteViewModel removeFavoriteViewModel;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_product);

        // Initialiser les vues
        buy_now = findViewById(R.id.buy_now);
        title = findViewById(R.id.title);
        price = findViewById(R.id.price);

        // Tentative de trouver le bouton favoris s'il existe
        try {
            favoriteButton = findViewById(R.id.favorite_button);
        } catch (Exception e) {
            // Le bouton n'existe pas encore dans la mise en page
        }

        // Initialiser les ViewModels
        addFavoriteViewModel = new ViewModelProvider(this).get(AddFavoriteViewModel.class);
        removeFavoriteViewModel = new ViewModelProvider(this).get(RemoveFavoriteViewModel.class);

        // Récupérer le produit depuis l'Intent
        if (getIntent().hasExtra("product")) {
            currentProduct = getIntent().getParcelableExtra("product");
            if (currentProduct != null) {
                displayProductDetails();
            }
        }

        buy_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentProduct != null) {
                    Intent intent = new Intent(ShowProduct.this, OrderPlacing.class);
                    intent.putExtra("product", currentProduct);
                    startActivity(intent);
                } else {
                    Toast.makeText(ShowProduct.this, "Produit non disponible", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (favoriteButton != null) {
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!LoginUtils.getInstance(ShowProduct.this).isLoggedIn()) {
                        Toast.makeText(ShowProduct.this, "Veuillez vous connecter pour ajouter des favoris", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    toggleFavorite();
                }
            });
        }
    }

    private void displayProductDetails() {
        title.setText(currentProduct.getProductName());
        price.setText("₹" + currentProduct.getProductPrice());

        // Définir l'état initial du bouton favori
        isFavorite = currentProduct.isFavourite() == 1;
        updateFavoriteButtonUI();
    }

    private void toggleFavorite() {
        if (favoriteButton == null || currentProduct == null) return;

        int userId = LoginUtils.getInstance(this).getUserId();
        if (userId == -1) return;

        if (isFavorite) {
            // Supprimer des favoris
            removeFavoriteViewModel.removeFavorite(userId, currentProduct.getProductId(), new RequestCallback() {
                @Override
                public void onCallBack() {
                    isFavorite = false;
                    currentProduct.setIsFavourite(false);
                    updateFavoriteButtonUI();
                    Toast.makeText(ShowProduct.this, "Retiré des favoris", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Ajouter aux favoris
            Favorite favorite = new Favorite(userId, currentProduct.getProductId());
            addFavoriteViewModel.addFavorite(favorite, new RequestCallback() {
                @Override
                public void onCallBack() {
                    isFavorite = true;
                    currentProduct.setIsFavourite(true);
                    updateFavoriteButtonUI();
                    Toast.makeText(ShowProduct.this, "Ajouté aux favoris", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateFavoriteButtonUI() {
        if (favoriteButton != null) {
            favoriteButton.setImageResource(isFavorite ?
                    R.drawable.favorite_like : R.drawable.favorite_unlike);
        }
    }
}
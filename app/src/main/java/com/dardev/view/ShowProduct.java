package com.dardev.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.dardev.R;
import com.dardev.ViewModel.AddFavoriteViewModel;
import com.dardev.ViewModel.AddToCartViewModel;
import com.dardev.ViewModel.RemoveFavoriteViewModel;
import com.dardev.model.Cart;
import com.dardev.model.Favorite;
import com.dardev.model.Product;
import com.dardev.utils.LoginUtils;
import com.dardev.utils.RequestCallback;

import okhttp3.ResponseBody;

public class ShowProduct extends AppCompatActivity {
    private static final String TAG = "ShowProduct";

    Button buy_now, addToCartButton;
    ImageButton favoriteButton;
    TextView title, price;

    private Product currentProduct;
    private AddFavoriteViewModel addFavoriteViewModel;
    private RemoveFavoriteViewModel removeFavoriteViewModel;
    private AddToCartViewModel addToCartViewModel;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_product);

        // Initialiser les vues
        buy_now = findViewById(R.id.buy_now);
        title = findViewById(R.id.title);
        price = findViewById(R.id.price);
        addToCartButton = findViewById(R.id.add_to_cart_button);

        // Tentative de trouver le bouton favoris s'il existe
        try {
            favoriteButton = findViewById(R.id.favorite_button);
        } catch (Exception e) {
            // Le bouton n'existe pas encore dans la mise en page
        }

        // Initialiser les ViewModels
        addFavoriteViewModel = new ViewModelProvider(this).get(AddFavoriteViewModel.class);
        removeFavoriteViewModel = new ViewModelProvider(this).get(RemoveFavoriteViewModel.class);
        addToCartViewModel = new ViewModelProvider(this).get(AddToCartViewModel.class);

        // Récupérer le produit depuis l'Intent
        if (getIntent().hasExtra("product")) {
            currentProduct = getIntent().getParcelableExtra("product");
            if (currentProduct != null) {
                displayProductDetails();
            }
        }

        // Listener pour le bouton Buy Now
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

        // Listener pour le bouton Add to Cart
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductToCart();
            }
        });

        // Listener pour le bouton Favoris
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

    private void addProductToCart() {
        // Vérifier si l'utilisateur est connecté
        if (!LoginUtils.getInstance(this).isLoggedIn()) {
            Toast.makeText(this, "Veuillez vous connecter pour ajouter au panier", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifier si le produit est disponible
        if (currentProduct == null) {
            Toast.makeText(this, "Produit non disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = LoginUtils.getInstance(this).getUserId();
        if (userId == -1) {
            Toast.makeText(this, "Erreur de session utilisateur", Toast.LENGTH_SHORT).show();
            return;
        }

        // Créer l'objet Cart
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(currentProduct.getProductId());
        cart.setQuantity(1); // Quantité par défaut

        // Désactiver temporairement le bouton pour éviter les clics multiples
        addToCartButton.setEnabled(false);
        addToCartButton.setText("Adding...");

        // Appeler l'API via le ViewModel
        addToCartViewModel.addToCart(cart, new RequestCallback() {
            @Override
            public void onCallBack() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Réactiver le bouton
                        addToCartButton.setEnabled(true);
                        addToCartButton.setText("Add to Cart");

                        Toast.makeText(ShowProduct.this, "Produit ajouté au panier!", Toast.LENGTH_SHORT).show();

                        // Animation de succès
                        animateAddToCartSuccess();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Réactiver le bouton
                        addToCartButton.setEnabled(true);
                        addToCartButton.setText("Add to Cart");

                        Toast.makeText(ShowProduct.this, "Erreur: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).observe(this, new Observer<ResponseBody>() {
            @Override
            public void onChanged(ResponseBody responseBody) {
                // Cette méthode sera appelée quand la réponse est reçue
                if (responseBody != null) {
                    // Succès - les actions sont déjà gérées dans le callback
                } else {
                    // Erreur - réactiver le bouton
                    addToCartButton.setEnabled(true);
                    addToCartButton.setText("Add to Cart");
                }
            }
        });
    }

    private void animateAddToCartSuccess() {
        // Animation simple pour indiquer le succès
        addToCartButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        addToCartButton.setText("Added ✓");

        // Remettre l'apparence normale après 2 secondes
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addToCartButton.setBackgroundResource(R.drawable.continue2_background); // Ou votre background personnalisé
                addToCartButton.setText("Add to Cart");
            }
        }, 2000);
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

                @Override
                public void onError(String error) {

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

                @Override
                public void onError(String error) {

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
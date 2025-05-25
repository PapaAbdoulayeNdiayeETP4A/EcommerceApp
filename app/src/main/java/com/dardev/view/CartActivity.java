package com.dardev.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.dardev.R;
import com.dardev.ViewModel.CartViewModel;
import com.dardev.adapter.CartAdapter;
import com.dardev.databinding.CartBinding;
import com.dardev.model.Product;
import com.dardev.utils.LoginUtils;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnItemDeleteListener {
    private static final String TAG = "CartActivity";
    CartBinding cartBinding;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    Button continue_button;
    CartViewModel cartViewModel;
    CartAdapter cartAdapter;

    // Ajout des TextView pour le total
    private TextView totalItemsText;
    private TextView totalPrice;

    private List<Product> cartList;
    private double cartTotal = 0.0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cartBinding = DataBindingUtil.setContentView(this, R.layout.cart);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white, getTheme()));

        recyclerView = cartBinding.recyclerview;
        continue_button = cartBinding.continueButton;
        totalItemsText = cartBinding.totalItemsText;
        totalPrice = cartBinding.totalPrice;

        // Initialiser la liste vide
        cartList = new ArrayList<>();

        setUpRecyclerView();
        getProductsInCart();

        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cartList != null && !cartList.isEmpty()) {
                    Intent intent = new Intent(CartActivity.this, OrderPlacing.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(CartActivity.this, "Votre panier est vide", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setUpRecyclerView() {
        linearLayoutManager = new LinearLayoutManager(CartActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        // Initialiser l'adaptateur avec une liste vide
        cartAdapter = new CartAdapter(recyclerView, CartActivity.this, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        cartBinding.recyclerview.setAdapter(cartAdapter);
    }

    private void getProductsInCart() {
        try {
            // Vérifier si l'utilisateur est connecté
            if (LoginUtils.getInstance(this).isLoggedIn()) {
                int userId = LoginUtils.getInstance(this).getUserId();

                cartViewModel.getProductsInCart(userId).observe(this, cartApiResponse -> {
                    if (cartApiResponse != null && cartApiResponse.getProductsInCart() != null) {
                        cartList = cartApiResponse.getProductsInCart();

                        if (cartList.size() == 0) {
                            // Panier vide, mettre à jour l'UI
                            updateCartTotals(0, 0.0);
                            Toast.makeText(CartActivity.this, "Votre panier est vide", Toast.LENGTH_SHORT).show();
                        } else {
                            // Créer et configurer l'adaptateur avec les données réelles
                            updateCartAdapter(cartList);

                            // Calculer le total
                            calculateCartTotal(cartList);
                        }
                    } else {
                        Toast.makeText(CartActivity.this, "Erreur lors du chargement du panier", Toast.LENGTH_SHORT).show();
                        updateCartTotals(0, 0.0);
                        loadStaticCartData();
                    }
                });
            } else {
                Toast.makeText(CartActivity.this, "Veuillez vous connecter pour voir votre panier", Toast.LENGTH_SHORT).show();
                updateCartTotals(0, 0.0);
                loadStaticCartData();
            }
        } catch (Exception e) {
            Toast.makeText(CartActivity.this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            updateCartTotals(0, 0.0);
            loadStaticCartData();
        }
    }

    private void calculateCartTotal(List<Product> products) {
        cartTotal = 0.0;
        for (Product product : products) {
            cartTotal += product.getProductPrice();
        }
        updateCartTotals(products.size(), cartTotal);
    }

    private void updateCartTotals(int itemCount, double total) {
        // Mettre à jour le texte du nombre d'articles
        totalItemsText.setText("Total Price (" + itemCount + " items)");

        // Mettre à jour le prix total en FCFA
        totalPrice.setText(String.format("%.0f FCFA", total));
    }

    private void updateCartAdapter(List<Product> products) {
        ArrayList<String> imageUrls = new ArrayList<>(); // Changé de Integer à String pour les URLs
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> prices = new ArrayList<>();

        for (Product product : products) {
            // Utiliser l'URL réelle de l'image du produit
            imageUrls.add(product.getProductImage());
            titles.add(product.getProductName());
            prices.add(String.format("%.0f FCFA", product.getProductPrice()));
        }

        // Créer un nouvel adaptateur avec les URLs d'images réelles et le listener de suppression
        cartAdapter = new CartAdapter(recyclerView, CartActivity.this, imageUrls, titles, prices, this);
        cartBinding.recyclerview.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemDelete(int position) {
        if (cartList != null && position >= 0 && position < cartList.size()) {
            // Récupérer le produit à supprimer
            Product productToRemove = cartList.get(position);

            // Supprimer de la liste locale
            cartList.remove(position);

            // Supprimer du serveur si l'utilisateur est connecté
            if (LoginUtils.getInstance(this).isLoggedIn()) {
                int userId = LoginUtils.getInstance(this).getUserId();
                removeProductFromCart(userId, productToRemove.getProductId());
            }

            // Mettre à jour l'adaptateur
            cartAdapter.removeItem(position);

            // Recalculer les totaux
            calculateCartTotal(cartList);

            // Afficher un message de confirmation
            Toast.makeText(this, "Produit supprimé du panier", Toast.LENGTH_SHORT).show();

            // Si le panier est vide, afficher le message approprié
            if (cartList.isEmpty()) {
                Toast.makeText(this, "Votre panier est vide", Toast.LENGTH_SHORT).show();
                updateCartTotals(0, 0.0);
            }
        }
    }

    private void removeProductFromCart(int userId, int productId) {
        // Utiliser le ViewModel pour supprimer le produit du serveur
        try {
            cartViewModel.removeFromCart(userId, productId).observe(this, response -> {
                if (response != null) {
                    // Succès de la suppression côté serveur
                    android.util.Log.d(TAG, "Produit supprimé avec succès du serveur");
                } else {
                    // Erreur lors de la suppression côté serveur
                    android.util.Log.e(TAG, "Erreur lors de la suppression côté serveur");
                    Toast.makeText(this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            android.util.Log.e(TAG, "Erreur lors de la suppression: " + e.getMessage());
            Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadStaticCartData() {
        // Méthode de secours pour charger des données statiques
        ArrayList<String> staticImages = new ArrayList<>();
        ArrayList<String> staticTitles = new ArrayList<>();
        ArrayList<String> staticPrices = new ArrayList<>();

        // Utiliser des URLs d'images par défaut ou des images locales
        staticImages.add("https://example.com/default-shoe1.jpg"); // ou une URL réelle
        staticImages.add("https://example.com/default-shoe2.jpg"); // ou une URL réelle

        staticTitles.add("Asian WNDR-13 Running Shoes for Men(Green, Grey)");
        staticTitles.add("Asian WNDR-13 Running Shoes for Men(Green, Grey)");

        staticPrices.add("15000 FCFA");
        staticPrices.add("25000 FCFA");

        cartAdapter = new CartAdapter(recyclerView, CartActivity.this, staticImages, staticTitles, staticPrices);
        cartBinding.recyclerview.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        // Mettre à jour les totaux avec les données statiques
        updateCartTotals(2, 40000.0);
    }
}
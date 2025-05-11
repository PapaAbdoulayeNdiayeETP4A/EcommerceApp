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

public class CartActivity extends AppCompatActivity {
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
        ArrayList<Integer> images = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> prices = new ArrayList<>();

        for (Product product : products) {
            // Convertir l'URL de l'image en ressource drawable (ou charger avec Glide/Picasso)
            // Pour simplifier, utilisez une image par défaut
            images.add(R.drawable.shoes1);
            titles.add(product.getProductName());
            prices.add(String.format("%.0f FCFA", product.getProductPrice()));
        }

        cartAdapter = new CartAdapter(recyclerView, CartActivity.this, images, titles, prices);
        cartBinding.recyclerview.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();
    }

    private void loadStaticCartData() {
        // Méthode de secours pour charger des données statiques
        cartAdapter = new CartAdapter(recyclerView, CartActivity.this, new ArrayList<Integer>(), new ArrayList<String>(), new ArrayList<String>());
        cartAdapter.update(R.drawable.shoes1, "Asian WNDR-13 Running Shoes for Men(Green, Grey)", "15000 FCFA");
        cartAdapter.update(R.drawable.shoes2, "Asian WNDR-13 Running Shoes for Men(Green, Grey)", "25000 FCFA");
        cartBinding.recyclerview.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        // Mettre à jour les totaux avec les données statiques
        updateCartTotals(2, 40000.0);
    }
}
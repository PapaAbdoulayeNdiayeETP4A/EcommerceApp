package com.dardev.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.dardev.utils.LoginUtils; // Assurez-vous que cette classe existe

public class CartActivity extends AppCompatActivity
{
    private static final String TAG = "CartActivity";
    CartBinding cartBinding;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    Button continue_button;
    CartViewModel cartViewModel;
    CartAdapter cartAdapter;

    private List<Product> cartList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        cartBinding = DataBindingUtil.setContentView(this, R.layout.cart);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white, getTheme()));

        recyclerView = cartBinding.recyclerview;
        continue_button = cartBinding.continueButton;

        // Initialisez la liste vide
        cartList = new ArrayList<>();

        setUpRecyclerView();
        getProductsInCart();

        continue_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (cartList != null && !cartList.isEmpty()) {
                    Intent intent = new Intent(CartActivity.this, OrderPlacing.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(CartActivity.this, "Votre panier est vide", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setUpRecyclerView()
    {
        linearLayoutManager = new LinearLayoutManager(CartActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        // Initialiser l'adaptateur avec une liste vide
        cartAdapter = new CartAdapter(recyclerView, CartActivity.this, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        cartBinding.recyclerview.setAdapter(cartAdapter);
    }

    private void getProductsInCart()
    {
        // Afficher un indicateur de chargement si vous en avez un
        // cartBinding.loadingIndicator.setVisibility(View.VISIBLE);

        try {
            // Vérifier si l'utilisateur est connecté
            int userId = LoginUtils.getInstance(this).getUserInfo().getId();

            cartViewModel.getProductsInCart(userId).observe(this, cartApiResponse -> {
                // Masquer l'indicateur de chargement
                // cartBinding.loadingIndicator.setVisibility(View.GONE);

                if (cartApiResponse != null && cartApiResponse.getProductsInCart() != null) {
                    cartList = cartApiResponse.getProductsInCart();

                    if (cartList.size() == 0) {
                        // Montrer un message "panier vide" si vous en avez un
                        // cartBinding.emptyCart.setVisibility(View.VISIBLE);
                        Toast.makeText(CartActivity.this, "Votre panier est vide", Toast.LENGTH_SHORT).show();
                    } else {
                        // Créer et configurer l'adaptateur avec les données réelles
                        updateCartAdapter(cartList);
                    }
                } else {
                    Toast.makeText(CartActivity.this, "Erreur lors du chargement du panier", Toast.LENGTH_SHORT).show();
                    // En cas d'erreur, vous pouvez charger des données statiques pour les tests
                    loadStaticCartData();
                }
            });
        } catch (Exception e) {
            // En cas d'erreur (par exemple, utilisateur non connecté)
            Toast.makeText(CartActivity.this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            // cartBinding.loadingIndicator.setVisibility(View.GONE);
            loadStaticCartData();
        }
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
            prices.add("₹" + product.getProductPrice());
        }

        cartAdapter = new CartAdapter(recyclerView, CartActivity.this, images, titles, prices);
        cartBinding.recyclerview.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();
    }

    private void loadStaticCartData() {
        // Méthode de secours pour charger des données statiques
        cartAdapter = new CartAdapter(recyclerView, CartActivity.this, new ArrayList<Integer>(), new ArrayList<String>(), new ArrayList<String>());
        cartAdapter.update(R.drawable.shoes1, "Asian WNDR-13 Running Shoes for Men(Green, Grey)", "₹300.00");
        cartAdapter.update(R.drawable.shoes2, "Asian WNDR-13 Running Shoes for Men(Green, Grey)", "₹500.00");
        cartBinding.recyclerview.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();
    }
}
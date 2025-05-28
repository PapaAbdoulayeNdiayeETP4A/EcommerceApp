package com.dardev.view;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.dardev.R;
import com.dardev.ViewModel.OrderViewModel;
import com.dardev.databinding.OrderPlacingBinding;
import com.dardev.model.Cart;
import com.dardev.model.CartApiResponse;
import com.dardev.model.Ordering;
import com.dardev.model.OrderItem;
import com.dardev.model.Product;
import com.dardev.net.Api;
import com.dardev.net.RetrofitClient;
import com.dardev.utils.LoginUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderPlacing extends AppCompatActivity {
    private static final String TAG = "OrderPlacing";

    OrderPlacingBinding binding;
    SeekBar seekBar;
    Button deliver_here, placeOrderButton;
    LinearLayout address, order_summary;
    ConstraintLayout payment;
    RadioGroup paymentMethodRadioGroup;
    TextView orderSummaryTotalTextView;
    ProgressBar progressBar;

    private OrderViewModel orderViewModel;
    private List<Product> productsInCart; // Changé de Cart à Product
    private double totalPrice = 0.0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.order_placing);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white, getTheme()));

        seekBar = binding.seekbar;
        deliver_here = binding.deliverHere;
        placeOrderButton = binding.continueButton;
        address = binding.address;
        order_summary = binding.orderSummary;
        payment = binding.payment;
        paymentMethodRadioGroup = binding.paymentMethodGroup;
        orderSummaryTotalTextView = binding.summaryTotal;
        progressBar = binding.progressBar;

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        // Désactivez la SeekBar de l'interaction utilisateur
        seekBar.setEnabled(false);
        seekBar.setProgress(0);

        // Masquer les sections initialement
        order_summary.setVisibility(View.GONE);
        payment.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        // Récupérer les articles du panier dès le démarrage de l'activité
        fetchCartItems();

        deliver_here.setOnClickListener(v -> {
            Toast.makeText(this, "Adresse de livraison sélectionnée.", Toast.LENGTH_SHORT).show();
            seekBar.setProgress(50);
            order_summary.setVisibility(View.VISIBLE);
            payment.setVisibility(View.VISIBLE);
            updateOrderSummary();
        });

        placeOrderButton.setOnClickListener(v -> placeOrder());
    }

    private void fetchCartItems() {
        int userId = LoginUtils.getInstance(this).getUserId();
        if (userId != -1) {
            progressBar.setVisibility(View.VISIBLE);
            RetrofitClient.getInstance().getApi().getProductsInCart(userId).enqueue(new Callback<CartApiResponse>() {
                @Override
                public void onResponse(Call<CartApiResponse> call, Response<CartApiResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        productsInCart = response.body().getProductsInCart(); // Stocker dans productsInCart
                        if (productsInCart != null && !productsInCart.isEmpty()) {
                            Toast.makeText(OrderPlacing.this, productsInCart.size() + " articles dans le panier.", Toast.LENGTH_SHORT).show();
                            // Calculer le total dès maintenant
                            calculateTotal();
                        } else {
                            Toast.makeText(OrderPlacing.this, "Votre panier est vide.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(OrderPlacing.this, "Erreur lors de la récupération du panier.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Erreur lors de la récupération du panier: " + response.code());
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<CartApiResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(OrderPlacing.this, "Échec de la connexion pour le panier: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Échec de la récupération du panier: " + t.getMessage());
                    finish();
                }
            });
        } else {
            Toast.makeText(this, "Veuillez vous connecter.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void calculateTotal() {
        totalPrice = 0.0;
        if (productsInCart != null) {
            for (Product product : productsInCart) {
                totalPrice += product.getProductPrice(); // Supposant que chaque produit a une quantité de 1
                // Si vous avez un champ quantité dans Product, utilisez: totalPrice += product.getProductPrice() * product.getQuantity();
            }
        }
    }

    private void updateOrderSummary() {
        if (productsInCart == null || productsInCart.isEmpty()) {
            orderSummaryTotalTextView.setText("Total: 0 FCFA");
            return;
        }

        // Effacer le contenu existant de order_summary (garder seulement le total à la fin)
        order_summary.removeAllViews();

        // Ajouter chaque produit du panier
        for (Product product : productsInCart) {
            // Créer un LinearLayout horizontal pour chaque produit
            LinearLayout productLayout = new LinearLayout(this);
            productLayout.setOrientation(LinearLayout.HORIZONTAL);
            productLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            productLayout.setPadding(0, 0, 0, 16); // Espacement entre les produits

            // Nom du produit (côté gauche)
            TextView productName = new TextView(this);
            productName.setText(product.getProductName());
            productName.setTextSize(14);
            productName.setTextColor(getResources().getColor(R.color.black, getTheme()));
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
            );
            productName.setLayoutParams(nameParams);

            // Prix du produit (côté droit)
            TextView productPrice = new TextView(this);
            productPrice.setText(String.format("%.0f FCFA", product.getProductPrice()));
            productPrice.setTextSize(14);
            productPrice.setTextColor(getResources().getColor(R.color.black, getTheme()));

            // Ajouter les TextViews au layout du produit
            productLayout.addView(productName);
            productLayout.addView(productPrice);

            // Ajouter le layout du produit au order_summary
            order_summary.addView(productLayout);
        }

        // Ajouter une ligne de séparation
        View separator = new View(this);
        separator.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2
        ));
        separator.setBackgroundColor(getResources().getColor(R.color.grey, getTheme()));
        LinearLayout.LayoutParams separatorParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2
        );
        separatorParams.setMargins(0, 16, 0, 16);
        separator.setLayoutParams(separatorParams);
        order_summary.addView(separator);

        // Ajouter le total final
        LinearLayout totalLayout = new LinearLayout(this);
        totalLayout.setOrientation(LinearLayout.HORIZONTAL);
        totalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView totalLabel = new TextView(this);
        totalLabel.setText("Total");
        totalLabel.setTextSize(18);
        totalLabel.setTextColor(getResources().getColor(R.color.black, getTheme()));
        totalLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams totalLabelParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        );
        totalLabel.setLayoutParams(totalLabelParams);

        TextView totalPriceView = new TextView(this);
        totalPriceView.setText(String.format("%.0f FCFA", totalPrice));
        totalPriceView.setTextSize(18);
        totalPriceView.setTextColor(getResources().getColor(R.color.black, getTheme()));
        totalPriceView.setTypeface(null, android.graphics.Typeface.BOLD);

        totalLayout.addView(totalLabel);
        totalLayout.addView(totalPriceView);
        order_summary.addView(totalLayout);

        // Mettre à jour aussi le TextView du total (pour garder la compatibilité)
        orderSummaryTotalTextView.setText(String.format("%.0f FCFA", totalPrice));
    }

    private void placeOrder() {
        int userId = LoginUtils.getInstance(this).getUserId();
        if (userId == -1) {
            Toast.makeText(this, "Veuillez vous connecter pour passer commande.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (productsInCart == null || productsInCart.isEmpty()) { // Vérifier productsInCart au lieu de cartItems
            Toast.makeText(this, "Votre panier est vide. Impossible de passer commande.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Récupérer le mode de paiement sélectionné
        int selectedPaymentMethodId = paymentMethodRadioGroup.getCheckedRadioButtonId();
        if (selectedPaymentMethodId == -1) {
            Toast.makeText(this, "Veuillez sélectionner un mode de paiement.", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedPaymentMethodId);
        String paymentMethod = selectedRadioButton.getText().toString();

        // Construire la liste des OrderItem à partir des Product
        List<OrderItem> orderItems = new ArrayList<>();
        for (Product product : productsInCart) {
            // Supposant une quantité de 1 pour chaque produit. Ajustez selon votre logique.
            int quantity = 1; // Ou récupérez la quantité réelle si elle existe dans votre modèle
            orderItems.add(new OrderItem(0, product.getProductId(), quantity, product.getProductPrice()));
        }

        // Supposons un shippingId fixe pour le moment
        int shippingId = 1;

        Ordering ordering = new Ordering(userId, shippingId, paymentMethod, totalPrice, orderItems);

        progressBar.setVisibility(View.VISIBLE);

        orderViewModel.placeOrder(ordering).observe(this, responseBody -> {
            progressBar.setVisibility(View.GONE);
            if (responseBody != null) {
                Toast.makeText(OrderPlacing.this, "Commande passée avec succès!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(OrderPlacing.this, MyOrdersActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(OrderPlacing.this, "Échec de la commande. Veuillez réessayer.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Échec de la passation de commande.");
            }
        });
    }
}
package com.dardev.view;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar; // Ajoutez ProgressBar
import android.widget.RadioButton; // Ajoutez RadioButton
import android.widget.RadioGroup; // Ajoutez RadioGroup
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.dardev.R;
import com.dardev.ViewModel.OrderViewModel; // Importez le ViewModel
import com.dardev.databinding.OrderPlacingBinding; // Assurez-vous que ce binding est correctement généré
import com.dardev.model.Cart; // Importez le modèle Cart si vous récupérez les items du panier
import com.dardev.model.CartApiResponse;
import com.dardev.model.Ordering; // Importez le modèle Ordering
import com.dardev.model.OrderItem; // Importez le modèle OrderItem
// import com.dardev.net.ApiClient; // Supprimez cette importation
import com.dardev.model.Product;
import com.dardev.net.Api; // Importez Api
import com.dardev.net.RetrofitClient; // Importez RetrofitClient pour obtenir l'instance d'Api
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
    Button deliver_here, placeOrderButton; // Renommé continue_button en placeOrderButton pour plus de clarté
    LinearLayout address, order_summary; // linearLayout2 supprimé car il était pour le texte de progression de la SeekBar
    ConstraintLayout payment;
    RadioGroup paymentMethodRadioGroup;
    TextView orderSummaryTotalTextView;
    ProgressBar progressBar; // Déclaré ici

    private OrderViewModel orderViewModel;
    private List<Cart> cartItems; // Pour stocker les articles du panier

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.order_placing);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white, getTheme()));

        seekBar = binding.seekbar;
        deliver_here = binding.deliverHere;
        placeOrderButton = binding.continueButton; // Utilisez continue_button du XML comme bouton final de passation de commande
        address = binding.address;
        order_summary = binding.orderSummary;
        // linearLayout2 = binding.linearlayout2; // Supprimé car il n'est plus utilisé directement
        payment = binding.payment;
        paymentMethodRadioGroup = binding.paymentMethodGroup; // ID corrigé
        orderSummaryTotalTextView = binding.summaryTotal; // ID corrigé
        // placeOrderButton = binding.placeOrderButton; // Ce bouton n'existe pas dans votre XML, utilisez continue_button à la place
        progressBar = binding.progressBar; // Initialisé ici avec l'ID du XML

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        // Désactivez la SeekBar de l'interaction utilisateur
        seekBar.setEnabled(false);
        // Définir la progression initiale
        seekBar.setProgress(0); // État initial : sélection de l'adresse

        // Masquer les sections initialement
        order_summary.setVisibility(View.GONE);
        payment.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE); // Masquer la ProgressBar au démarrage

        // Récupérer les articles du panier dès le démarrage de l'activité
        fetchCartItems();

        deliver_here.setOnClickListener(v -> {
            Toast.makeText(this, "Adresse de livraison sélectionnée.", Toast.LENGTH_SHORT).show();
            seekBar.setProgress(50); // Progression
            order_summary.setVisibility(View.VISIBLE); // Afficher le résumé de la commande
            payment.setVisibility(View.VISIBLE); // Afficher la section de paiement après la sélection de l'adresse
            // linearLayout2.setVisibility(View.VISIBLE); // Si vous voulez afficher la barre de progression de la livraison
            updateOrderSummary();
        });

        // Le bouton "continue_button" est maintenant le bouton "Place Order" final
        placeOrderButton.setOnClickListener(v -> placeOrder());

        // Initialisation de la SeekBar (si elle est utilisée pour les étapes)
        // Les écouteurs de SeekBar originaux semblent être commentés, donc je n'y touche pas.
        // Si vous avez des étapes visuelles, assurez-vous que la SeekBar reflète la progression (adresse, résumé, paiement)
        // seekBar.setProgress(0); // État initial: sélectionner l'adresse (déjà fait au début)
        // address.setVisibility(View.VISIBLE); // Afficher la section d'adresse (déjà visible par défaut)
    }

    private void fetchCartItems() {
        int userId = LoginUtils.getInstance(this).getUserId(); // Appel correct de la méthode d'instance
        if (userId != -1) {
            progressBar.setVisibility(View.VISIBLE);
            // Utilisez RetrofitClient.getInstance().getApi() pour obtenir l'instance de l'API
            RetrofitClient.getInstance().getApi().getProductsInCart(userId).enqueue(new Callback<CartApiResponse>() {
                @Override
                public void onResponse(Call<CartApiResponse> call, Response<CartApiResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        List<Product> productsInCart = response.body().getProductsInCart();
                        if (productsInCart != null && !productsInCart.isEmpty()) {
                            // Convertir les Product en Cart si nécessaire
                            // Ou modifier la logique pour utiliser directement les produits
                            Toast.makeText(OrderPlacing.this, productsInCart.size() + " articles dans le panier.", Toast.LENGTH_SHORT).show();
                            // updateOrderSummary(); // Vous devrez adapter cette méthode
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

    private void updateOrderSummary() {
        double total = 0;
        if (cartItems != null) {
            for (Cart item : cartItems) {
                // Assurez-vous que votre modèle Cart a une méthode getPrice()
                //total += item.getPrice() * item.getQuantity();
            }
        }
        orderSummaryTotalTextView.setText(String.format("Total: $%.2f", total));
    }

    private void placeOrder() {
        int userId = LoginUtils.getInstance(this).getUserId(); // Appel correct de la méthode d'instance
        if (userId == -1) {
            Toast.makeText(this, "Veuillez vous connecter pour passer commande.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cartItems == null || cartItems.isEmpty()) {
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
        String paymentMethod = selectedRadioButton.getText().toString(); // "Cash on Delivery" ou "Credit Card" ou "PayPal"

        // Récupérer le prix total du résumé
        double totalPrice = 0;
        try {
            String totalText = orderSummaryTotalTextView.getText().toString().replace("Total: $", "");
            totalPrice = Double.parseDouble(totalText);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Erreur lors de l'analyse du prix total: " + e.getMessage());
            Toast.makeText(this, "Erreur de calcul du total de la commande.", Toast.LENGTH_SHORT).show();
            return;
        }


        // Construire la liste des OrderItem à partir des Cart (assumant que Cart peut être converti en OrderItem)
        List<OrderItem> orderItems = new ArrayList<>();
        for (Cart cartItem : cartItems) {
            // Important: Votre OrderItem doit correspondre à ce que le backend attend.
            // Si OrderItem nécessite OrderId, vous devrez le définir côté backend ou après la création de la commande.
            // Pour l'instant, je ne mets pas OrderId car il n'est pas connu avant la création de la commande.
            orderItems.add(new OrderItem(0, cartItem.getProductId(), cartItem.getQuantity(), cartItem.getQuantity()));
        }

        // Supposons un shippingId fixe pour le moment, ou récupérez-le de l'adresse sélectionnée
        int shippingId = 1; // Remplacez par la logique réelle de sélection de l'adresse de livraison

        Ordering ordering = new Ordering(userId, shippingId, paymentMethod, totalPrice, orderItems);

        progressBar.setVisibility(View.VISIBLE); // Afficher la barre de progression

        orderViewModel.placeOrder(ordering).observe(this, responseBody -> {
            progressBar.setVisibility(View.GONE); // Cacher la barre de progression
            if (responseBody != null) {
                Toast.makeText(OrderPlacing.this, "Commande passée avec succès!", Toast.LENGTH_LONG).show();
                // Rediriger vers l'activité des commandes ou une confirmation
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

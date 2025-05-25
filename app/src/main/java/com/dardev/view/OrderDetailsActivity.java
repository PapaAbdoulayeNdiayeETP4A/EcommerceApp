package com.dardev.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dardev.R;
import com.dardev.ViewModel.OrderViewModel;
import com.dardev.adapter.OrderItemAdapter; // Vous aurez besoin de cet adaptateur
import com.dardev.databinding.OrderDetailsBinding; // Assurez-vous que ce binding est correctement généré
import com.dardev.model.Order;
import com.dardev.model.OrderItem;
import com.dardev.utils.LoginUtils;

public class OrderDetailsActivity extends AppCompatActivity {
    private static final String TAG = "OrderDetailsActivity";

    private OrderDetailsBinding orderDetailsBinding;
    private OrderViewModel orderViewModel;
    private OrderItemAdapter orderItemAdapter;
    private RecyclerView orderItemsRecyclerView;

    private int orderId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderDetailsBinding = DataBindingUtil.setContentView(this, R.layout.order_details);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white, getTheme()));

        orderItemsRecyclerView = orderDetailsBinding.orderItemsRecyclerView; // Assurez-vous que cet ID existe dans votre XML
        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Récupérer l'ID de la commande passée par l'Intent
        if (getIntent().hasExtra("ORDER_ID")) {
            orderId = getIntent().getIntExtra("ORDER_ID", -1);
        } else {
            Toast.makeText(this, "ID de commande non trouvé.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        loadOrderDetails();

        orderDetailsBinding.backButtonOrderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadOrderDetails() {
        int userId = LoginUtils.getInstance(this).getUserId(); // Récupérez l'ID de l'utilisateur connecté

        if (userId != -1 && orderId != -1) {
            orderViewModel.getOrderDetails(orderId, userId).observe(this, order -> {
                if (order != null) {
                    // Mettre à jour l'UI avec les détails de la commande
                    orderDetailsBinding.orderIdTextView.setText("Commande ID: " + order.getId());
                    orderDetailsBinding.orderDateTextView.setText("Date: " + order.getCreatedAt());
                    orderDetailsBinding.orderStatusTextView.setText("Statut: " + order.getStatus());
                    orderDetailsBinding.orderTotalPriceTextView.setText(String.format("Total: $%.2f", order.getTotalPrice()));
                    orderDetailsBinding.orderPaymentMethodTextView.setText("Méthode de paiement: " + order.getPaymentMethod());

                    // Afficher les articles de la commande
                    if (order.getItems() != null && !order.getItems().isEmpty()) {
                        orderItemAdapter = new OrderItemAdapter(order.getItems());
                        orderItemsRecyclerView.setAdapter(orderItemAdapter);
                    } else {
                        Toast.makeText(this, "Aucun article dans cette commande.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "Impossible de charger les détails de la commande.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Order details not found for order ID: " + orderId + " and user ID: " + userId);
                    finish(); // Fermer l'activité si la commande n'est pas trouvée
                }
            });
        } else {
            Toast.makeText(this, "Erreur: Utilisateur non connecté ou ID de commande invalide.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
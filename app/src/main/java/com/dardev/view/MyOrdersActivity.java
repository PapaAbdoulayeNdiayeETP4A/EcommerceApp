package com.dardev.view;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import com.dardev.R;
import com.dardev.ViewModel.OrderViewModel; // Importez votre nouveau ViewModel
import com.dardev.adapter.MyOrdersAdapter;
import com.dardev.databinding.MyOrdersBinding; // Assurez-vous que ce binding est correctement généré
import com.dardev.model.Order; // Importez la classe Order
import com.dardev.utils.LoginUtils; // Pour récupérer l'ID de l'utilisateur

public class MyOrdersActivity extends AppCompatActivity {
    private static final String TAG = "MyOrdersActivity";

    private RecyclerView recyclerView;
    private MyOrdersAdapter myOrdersAdapter;
    private MyOrdersBinding myOrdersBinding;
    private OrderViewModel orderViewModel; // Déclarez le ViewModel

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myOrdersBinding = DataBindingUtil.setContentView(this, R.layout.my_orders);

        recyclerView = myOrdersBinding.recyclerview;

        getWindow().setStatusBarColor(getResources().getColor(R.color.white, getTheme()));

        setUpRecyclerView();

        // Initialiser le OrderViewModel
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        // Appeler la méthode pour charger les commandes de l'utilisateur
        loadUserOrders();

        myOrdersBinding.backButtonMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setUpRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        // Initialiser l'adaptateur avec une liste vide et le bon type de données
        myOrdersAdapter = new MyOrdersAdapter(recyclerView, this, new ArrayList<>(), new MyOrdersAdapter.OrderAdapterOnClickHandler() {
            @Override
            public void onClick(Order order) {
                // Gérer le clic sur une commande pour afficher les détails
                Intent intent = new Intent(MyOrdersActivity.this, OrderDetailsActivity.class);
                intent.putExtra("ORDER_ID", order.getId()); // Passez l'ID de la commande
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(myOrdersAdapter);
    }

    private void loadUserOrders() {
        int userId = LoginUtils.getInstance(this).getUserId(); // Récupérez l'ID de l'utilisateur connecté

        if (userId != -1) { // Vérifiez si un utilisateur est connecté
            orderViewModel.getOrders(userId).observe(this, orders -> {
                if (orders != null && !orders.isEmpty()) {
                    myOrdersAdapter.setOrders(orders); // Utilisez la nouvelle méthode setOrders de l'adaptateur
                    Log.d(TAG, "Orders loaded successfully: " + orders.size() + " orders.");
                } else {
                    myOrdersAdapter.setOrders(new ArrayList<>()); // Efface la liste si aucune commande
                    Toast.makeText(MyOrdersActivity.this, "Aucune commande trouvée.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "No orders found for user " + userId);
                }
            });
        } else {
            Toast.makeText(this, "Veuillez vous connecter pour voir vos commandes.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "User not logged in. Cannot load orders.");
            // Rediriger vers l'écran de connexion si l'utilisateur n'est pas connecté
            // Intent loginIntent = new Intent(this, LoginActivity.class);
            // startActivity(loginIntent);
            // finish();
        }
    }
}
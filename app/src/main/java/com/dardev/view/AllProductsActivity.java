package com.dardev.view;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem; // Peut-être plus nécessaire si on ne gère pas un menu de toolbar
import android.view.View; // Ajouté pour le View.OnClickListener
import android.widget.ImageView; // Ajouté pour l'ImageView du bouton retour
import android.widget.TextView; // Ajouté pour le TextView du titre
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
// import androidx.appcompat.widget.Toolbar; // Plus besoin d'importer Toolbar si on ne l'utilise plus
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dardev.R;
import com.dardev.ViewModel.AddFavoriteViewModel;
import com.dardev.ViewModel.ProductViewModel;
import com.dardev.ViewModel.RemoveFavoriteViewModel;
import com.dardev.adapter.HomeProductAdapter;
import com.dardev.model.ProductApiResponse;
import com.dardev.utils.LoginUtils;
import com.dardev.utils.RequestCallback;

import java.util.ArrayList;

public class AllProductsActivity extends AppCompatActivity {

    private static final String TAG = "AllProductsActivity";
    private RecyclerView allProductsRecyclerView;
    private HomeProductAdapter productAdapter;
    private ProductViewModel productViewModel;
    private AddFavoriteViewModel addFavoriteViewModel;
    private RemoveFavoriteViewModel removeFavoriteViewModel;

    private ImageView backButton; // Déclarez l'ImageView
    private TextView titleTextView; // Déclarez le TextView pour le titre

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

        // Initialiser l'ImageView et le TextView de l'en-tête personnalisé
        backButton = findViewById(R.id.back_button_all_products);
        titleTextView = findViewById(R.id.title_all_products);

        // Définir le texte du titre
        titleTextView.setText("Tous les produits");

        // Gérer le clic sur le bouton retour
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Simule le comportement du bouton retour système
            }
        });

        // Supprimez les lignes suivantes qui initialisent la Toolbar par défaut
        // Toolbar toolbar = findViewById(R.id.toolbar_all_products);
        // setSupportActionBar(toolbar);
        // if (getSupportActionBar() != null) {
        //     getSupportActionBar().setTitle("Tous les produits");
        //     getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // }


        allProductsRecyclerView = findViewById(R.id.allProductsRecyclerView);
        allProductsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        addFavoriteViewModel = new ViewModelProvider(this).get(AddFavoriteViewModel.class);
        removeFavoriteViewModel = new ViewModelProvider(this).get(RemoveFavoriteViewModel.class);

        productAdapter = new HomeProductAdapter(this, new ArrayList<>(),
                addFavoriteViewModel, removeFavoriteViewModel);
        allProductsRecyclerView.setAdapter(productAdapter);

        loadAllProducts();
    }

    private void loadAllProducts() {
        int userId = -1;
        if (LoginUtils.getInstance(this).isLoggedIn()) {
            userId = LoginUtils.getInstance(this).getUserId();
        }

        productViewModel.getAllProducts().observe(this, productApiResponse -> {
            if (productApiResponse != null && productApiResponse.getProducts() != null) {
                productAdapter.setProductList(productApiResponse.getProducts());
                if (productApiResponse.getProducts().isEmpty()) {
                    Toast.makeText(this, "Aucun produit trouvé.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Erreur lors du chargement des produits.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "ProductApiResponse is null or products list is null.");
            }
        });
    }

    // La méthode onOptionsItemSelected n'est plus nécessaire pour le bouton retour si on gère un ImageView personnalisé
    // @Override
    // public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    //     if (item.getItemId() == android.R.id.home) {
    //         onBackPressed();
    //         return true;
    //     }
    //     return super.onOptionsItemSelected(item);
    // }
}
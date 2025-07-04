package com.dardev.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.dardev.R;
import com.dardev.ViewModel.FavoriteViewModel;
import com.dardev.adapter.WishlistAdapter;
import com.dardev.databinding.WishlistBinding;
import com.dardev.model.FavoriteApiResponse;
import com.dardev.model.Product;

import okhttp3.ResponseBody;

public class MyWishlistActivity extends AppCompatActivity {
    private static final String TAG = "WishlistActivity";

    private WishlistBinding binding;
    private RecyclerView recyclerView;
    private WishlistAdapter adapter;
    private FavoriteViewModel viewModel;
    private List<Product> wishlistProducts = new ArrayList<>();
    private int userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.wishlist);

        // Set status bar color
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, getTheme()));

        // Initialize RecyclerView
        recyclerView = binding.wishlistRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);

        // Get saved user ID from SharedPreferences
        userId = getUserId();

        // Setup adapter
        adapter = new WishlistAdapter(this, wishlistProducts, product -> {
            // Handle item click - navigate to product details
            // Intent intent = new Intent(MyWishlistActivity.this, ProductDetailsActivity.class);
            // intent.putExtra("PRODUCT", product);
            // startActivity(intent);
        });

        // Set listener for remove from wishlist
        adapter.setOnRemoveFromWishlistListener(new WishlistAdapter.OnRemoveFromWishlistListener() {
            @Override
            public void onRemoveFromWishlist(Product product, int position) {
                removeFromWishlist(product, position);
            }
        });

        recyclerView.setAdapter(adapter);

        // Load wishlist data
        loadWishlistData();

        // Setup continue shopping button
        binding.continueShoppingButton.setOnClickListener(v -> {
            // Navigate back to shopping or main activity
            finish();
        });
    }

    private void loadWishlistData() {
        // Observe wishlist data
        viewModel.getFavorites(userId).observe(this, new Observer<FavoriteApiResponse>() {
            @Override
            public void onChanged(FavoriteApiResponse response) {
                if (response != null && response.getFavorites() != null) {
                    wishlistProducts.clear();
                    wishlistProducts.addAll(response.getFavorites());
                    adapter.notifyDataSetChanged();

                    // Show empty state if no items
                    updateEmptyState();
                } else {
                    Log.e(TAG, "Failed to load wishlist data");
                    updateEmptyState();
                }
            }
        });
    }

    private void removeFromWishlist(Product product, int position) {
        // Appeler l'API pour supprimer de la base de données
        viewModel.removeFavorite(userId, product.getProductId()).observe(this, new Observer<ResponseBody>() {
            @Override
            public void onChanged(ResponseBody response) {
                if (response != null) {
                    // Suppression réussie
                    adapter.removeItem(position);
                    Toast.makeText(MyWishlistActivity.this, "Removed from wishlist", Toast.LENGTH_SHORT).show();

                    // Mettre à jour l'état vide
                    updateEmptyState();
                } else {
                    // Erreur lors de la suppression
                    Toast.makeText(MyWishlistActivity.this, "Failed to remove from wishlist", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to remove product from wishlist");
                }
            }
        });
    }

    private void updateEmptyState() {
        if (wishlistProducts.isEmpty()) {
            binding.emptyWishlistLayout.setVisibility(android.view.View.VISIBLE);
            binding.wishlistRecyclerView.setVisibility(android.view.View.GONE);
        } else {
            binding.emptyWishlistLayout.setVisibility(android.view.View.GONE);
            binding.wishlistRecyclerView.setVisibility(android.view.View.VISIBLE);
        }
    }

    private int getUserId() {
        // Get user ID from SharedPreferences
        return getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("user_id", 0);
    }
}
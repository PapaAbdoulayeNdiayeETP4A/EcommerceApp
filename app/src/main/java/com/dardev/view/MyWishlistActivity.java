package com.dardev.view;

import android.os.Bundle;
import android.util.Log;

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

public class MyWishlistActivity extends AppCompatActivity {
    private static final String TAG = "WishlistActivity";

    private WishlistBinding binding;
    private RecyclerView recyclerView;
    private WishlistAdapter adapter;
    private FavoriteViewModel viewModel;
    private List<Product> wishlistProducts = new ArrayList<>();

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

        // Setup adapter
        adapter = new WishlistAdapter(this, wishlistProducts, product -> {
            // Handle item click - navigate to product details
            // Intent intent = new Intent(MyWishlistActivity.this, ProductDetailsActivity.class);
            // intent.putExtra("PRODUCT", product);
            // startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        // Get saved user ID from SharedPreferences
        int userId = getUserId();

        // Observe wishlist data
        viewModel.getFavorites(userId).observe(this, new Observer<FavoriteApiResponse>() {
            @Override
            public void onChanged(FavoriteApiResponse response) {
                if (response != null && response.getFavorites() != null) {
                    wishlistProducts.clear();
                    wishlistProducts.addAll(response.getFavorites());
                    adapter.notifyDataSetChanged();

                    // Show empty state if no items
                    if (wishlistProducts.isEmpty()) {
                        binding.emptyWishlistLayout.setVisibility(android.view.View.VISIBLE);
                        binding.wishlistRecyclerView.setVisibility(android.view.View.GONE);
                    } else {
                        binding.emptyWishlistLayout.setVisibility(android.view.View.GONE);
                        binding.wishlistRecyclerView.setVisibility(android.view.View.VISIBLE);
                    }
                }
            }
        });
    }

    private int getUserId() {
        // Get user ID from SharedPreferences
        return getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("user_id", 0);
    }
}
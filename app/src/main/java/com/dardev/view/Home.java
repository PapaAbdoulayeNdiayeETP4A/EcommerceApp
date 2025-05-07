package com.dardev.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import com.dardev.R;
import com.dardev.SliderItem;
import com.dardev.ViewModel.AddFavoriteViewModel;
import com.dardev.ViewModel.ProductViewModel;
import com.dardev.ViewModel.RemoveFavoriteViewModel;
import com.dardev.adapter.HomeProductAdapter;
import com.dardev.adapter.slider_adapter;
import com.dardev.databinding.HomeBinding;
import com.dardev.model.Product;
import com.dardev.utils.LoginUtils;

public class Home extends Fragment {
    private static final String TAG = "HomeFragment";

    SearchView searchView;
    ImageView cart;

    SliderView sliderView;
    private slider_adapter sliderAdapter;
    private HomeProductAdapter productAdapter;

    HomeBinding homeBinding;
    DrawerLayout drawerLayout;

    // ViewModels
    private ProductViewModel productViewModel;
    private AddFavoriteViewModel addFavoriteViewModel;
    private RemoveFavoriteViewModel removeFavoriteViewModel;

    // RecyclerView
    private RecyclerView newArrivalsRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeBinding = DataBindingUtil.inflate(inflater, R.layout.home, container, false);
        View view = homeBinding.getRoot();

        cart = homeBinding.cart;
        searchView = homeBinding.searchView;
        sliderView = homeBinding.imageSlider;

        // Initialiser le RecyclerView pour les nouveaux produits
        newArrivalsRecyclerView = homeBinding.newArrivalsRecyclerView;
        newArrivalsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Initialiser les ViewModels
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        addFavoriteViewModel = new ViewModelProvider(this).get(AddFavoriteViewModel.class);
        removeFavoriteViewModel = new ViewModelProvider(this).get(RemoveFavoriteViewModel.class);

        // Charger les produits
        loadProducts();

        getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.purple));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getContext(), SearchResult.class);
                intent.putExtra("query", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CartActivity.class);
                startActivity(intent);
            }
        });

        // Configurer le slider
        setupSlider();

        return view;
    }

    private void loadProducts() {
        // Récupérer l'ID utilisateur si connecté
        int userId = -1;
        if (LoginUtils.getInstance(getContext()).isLoggedIn()) {
            userId = LoginUtils.getInstance(getContext()).getUserId();
        }

        // Initialiser l'adaptateur avec une liste vide
        productAdapter = new HomeProductAdapter(getContext(), new ArrayList<>(),
                addFavoriteViewModel, removeFavoriteViewModel);
        newArrivalsRecyclerView.setAdapter(productAdapter);

        // Charger les produits depuis l'API
        productViewModel.getProducts(1).observe(getViewLifecycleOwner(), productApiResponse -> {
            if (productApiResponse != null && productApiResponse.getProducts() != null) {
                List<Product> products = productApiResponse.getProducts();
                if (!products.isEmpty()) {
                    productAdapter.setProductList(products);
                } else {
                    Toast.makeText(getContext(), "Aucun produit disponible", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Erreur lors du chargement des produits", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSlider() {
        sliderAdapter = new slider_adapter(getContext());
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4);
        sliderView.startAutoCycle();

        sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                Log.i(TAG, "onIndicatorClicked: " + sliderView.getCurrentPagePosition());
            }
        });

        // Ajouter des éléments au slider
        addSliderItems();
    }

    private void addSliderItems() {
        List<SliderItem> sliderItemList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            SliderItem sliderItem = new SliderItem();
            sliderItem.setDescription("Slider Item " + i);
            if (i % 2 == 0) {
                sliderItem.setImageUrl("https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
            } else {
                sliderItem.setImageUrl("https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260");
            }
            sliderItemList.add(sliderItem);
        }
        sliderAdapter.renewItems(sliderItemList);
    }
}
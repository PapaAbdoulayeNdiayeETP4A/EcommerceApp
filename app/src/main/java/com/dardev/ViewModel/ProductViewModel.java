package com.dardev.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dardev.model.ProductApiResponse;
import com.dardev.repository.ProductRepository;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class ProductViewModel extends AndroidViewModel {
    private static final String TAG = "ProductViewModel";

    private ProductRepository productRepository;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application);
    }

    public LiveData<ResponseBody> insertProduct(Map<String, RequestBody> productInfo, MultipartBody.Part image) {
        Log.d(TAG, "Inserting new product");
        return productRepository.insertProduct(productInfo, image);
    }

    public LiveData<ProductApiResponse> getProducts(int page) {
        Log.d(TAG, "Getting products, page: " + page);
        return productRepository.getProducts(page);
    }

    public LiveData<ProductApiResponse> getAllProducts() {
       return productRepository.getAllProducts();
    }

    public LiveData<ProductApiResponse> getProductsByCategory(String category, int userId, int page) {
        Log.d(TAG, "Getting products by category: " + category + ", user: " + userId + ", page: " + page);
        return productRepository.getProductsByCategory(category, userId, page);
    }

    public LiveData<ProductApiResponse> searchForProduct(String keyword, int userId) {
        Log.d(TAG, "Searching for products with keyword: " + keyword + ", user: " + userId);
        return productRepository.searchForProduct(keyword, userId);
    }
}
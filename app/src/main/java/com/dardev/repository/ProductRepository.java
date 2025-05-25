package com.dardev.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dardev.model.ProductApiResponse;
import com.dardev.net.RetrofitClient;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    private static final String TAG = ProductRepository.class.getSimpleName();
    private Application application;

    public ProductRepository(Application application) {
        this.application = application;
    }

    public LiveData<ResponseBody> insertProduct(Map<String, RequestBody> productInfo, MultipartBody.Part image) {
        final MutableLiveData<ResponseBody> mutableLiveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApi().insertProduct(productInfo, image).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: " + response.code());

                ResponseBody responseBody = response.body();

                if (response.body() != null) {
                    mutableLiveData.setValue(responseBody);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

        return mutableLiveData;
    }

    public LiveData<ProductApiResponse> getProducts(int page) {
        final MutableLiveData<ProductApiResponse> mutableLiveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApi().getProducts(page).enqueue(new Callback<ProductApiResponse>() {
            @Override
            public void onResponse(Call<ProductApiResponse> call, Response<ProductApiResponse> response) {
                Log.d(TAG, "onResponse: Succeeded");

                ProductApiResponse productApiResponse = response.body();

                if (response.body() != null) {
                    mutableLiveData.setValue(productApiResponse);
                }
            }

            @Override
            public void onFailure(Call<ProductApiResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

        return mutableLiveData;
    }

    // Dans com.dardev.ViewModel.ProductViewModel.java
// ...
    public LiveData<ProductApiResponse> getAllProducts() {
        MutableLiveData<ProductApiResponse> data = new MutableLiveData<>();
        RetrofitClient.getInstance().getApi().getAllProducts().enqueue(new Callback<ProductApiResponse>() {
            @Override
            public void onResponse(Call<ProductApiResponse> call, Response<ProductApiResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null); // Gérer l'erreur
                }
            }

            @Override
            public void onFailure(Call<ProductApiResponse> call, Throwable t) {
                data.setValue(null); // Gérer l'échec
            }
        });
        return data;
    }
// ...

    public LiveData<ProductApiResponse> getProductsByCategory(String category, int userId, int page) {
        final MutableLiveData<ProductApiResponse> mutableLiveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApi().getProductsByCategory(category, userId, page).enqueue(new Callback<ProductApiResponse>() {
            @Override
            public void onResponse(Call<ProductApiResponse> call, Response<ProductApiResponse> response) {
                Log.d(TAG, "onResponse: Succeeded");

                ProductApiResponse productApiResponse = response.body();

                if (response.body() != null) {
                    mutableLiveData.setValue(productApiResponse);
                }
            }

            @Override
            public void onFailure(Call<ProductApiResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

        return mutableLiveData;
    }

    public LiveData<ProductApiResponse> searchForProduct(String keyword, int userId) {
        final MutableLiveData<ProductApiResponse> mutableLiveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApi().searchForProduct(keyword, userId).enqueue(new Callback<ProductApiResponse>() {
            @Override
            public void onResponse(Call<ProductApiResponse> call, Response<ProductApiResponse> response) {
                Log.d(TAG, "onResponse: Succeeded");

                ProductApiResponse productApiResponse = response.body();

                if (response.body() != null) {
                    mutableLiveData.setValue(productApiResponse);
                }
            }

            @Override
            public void onFailure(Call<ProductApiResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

        return mutableLiveData;
    }
}
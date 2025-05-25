package com.dardev.repository;


import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dardev.model.Cart;
import com.dardev.model.CartApiResponse;
import com.dardev.net.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository
{
    private static final String TAG = CartRepository.class.getSimpleName();
    private Application application;

    public CartRepository(Application application)
    {
        this.application = application;
    }

    public LiveData<CartApiResponse> getProductsInCart(int userId)
    {
        final MutableLiveData<CartApiResponse> mutableLiveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApi().getProductsInCart(userId).enqueue(new Callback<CartApiResponse>()
        {
            @Override
            public void onResponse(Call<CartApiResponse> call, Response<CartApiResponse> response)
            {
                Log.d(TAG, "onResponse: Succeeded");

                CartApiResponse cartApiResponse = response.body();

                if (response.body() != null)
                {
                    mutableLiveData.setValue(cartApiResponse);
                    Log.d(TAG, String.valueOf(response.body().getProductsInCart()));
                }
            }

            @Override
            public void onFailure(Call<CartApiResponse> call, Throwable t)
            {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
        return mutableLiveData;
    }

    public LiveData<ResponseBody> addToCart(Cart cart) {
        final MutableLiveData<ResponseBody> mutableLiveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApi().addToCart(cart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: " + response.code());

                if (response.isSuccessful()) {
                    mutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

        return mutableLiveData;
    }

    public LiveData<ResponseBody> removeFromCart(int userId, int productId) {
        final MutableLiveData<ResponseBody> mutableLiveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApi().removeFromCart(userId, productId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mutableLiveData.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mutableLiveData.setValue(null);
            }
        });

        return mutableLiveData;
    }
}
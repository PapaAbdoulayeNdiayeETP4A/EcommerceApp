package com.dardev.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dardev.model.OrderApiResponse;
import com.dardev.model.Ordering;
import com.dardev.model.Shipping;
import com.dardev.net.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private static final String TAG = OrderRepository.class.getSimpleName();
    private Application application;

    public OrderRepository(Application application) {
        this.application = application;
    }

    public LiveData<OrderApiResponse> getOrders(int userId) {
        final MutableLiveData<OrderApiResponse> mutableLiveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApi().getOrders(userId).enqueue(new Callback<OrderApiResponse>() {
            @Override
            public void onResponse(Call<OrderApiResponse> call, Response<OrderApiResponse> response) {
                Log.d(TAG, "onResponse: Succeeded");

                OrderApiResponse orderApiResponse = response.body();

                if (response.body() != null) {
                    mutableLiveData.setValue(orderApiResponse);
                }
            }

            @Override
            public void onFailure(Call<OrderApiResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

        return mutableLiveData;
    }

    public LiveData<ResponseBody> addShippingAddress(Shipping shipping) {
        final MutableLiveData<ResponseBody> mutableLiveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApi().addShippingAddress(shipping).enqueue(new Callback<ResponseBody>() {
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

    public LiveData<ResponseBody> orderProduct(Ordering ordering) {
        final MutableLiveData<ResponseBody> mutableLiveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApi().orderProduct(ordering).enqueue(new Callback<ResponseBody>() {
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
}
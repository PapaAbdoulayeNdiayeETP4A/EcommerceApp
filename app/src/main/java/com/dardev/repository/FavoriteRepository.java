package com.dardev.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dardev.model.Favorite;
import com.dardev.model.FavoriteApiResponse;
import com.dardev.net.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteRepository {
    private static final String TAG = FavoriteRepository.class.getSimpleName();
    private Application application;

    public FavoriteRepository(Application application) {
        this.application = application;
    }

    public LiveData<FavoriteApiResponse> getFavorites(int userId) {
        final MutableLiveData<FavoriteApiResponse> mutableLiveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApi().getFavorites(userId).enqueue(new Callback<FavoriteApiResponse>() {
            @Override
            public void onResponse(Call<FavoriteApiResponse> call, Response<FavoriteApiResponse> response) {
                Log.d(TAG, "onResponse: Succeeded");

                FavoriteApiResponse favoriteApiResponse = response.body();

                if (response.body() != null) {
                    mutableLiveData.setValue(favoriteApiResponse);
                }
            }

            @Override
            public void onFailure(Call<FavoriteApiResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

        return mutableLiveData;
    }

    public LiveData<ResponseBody> addFavorite(Favorite favorite) {
        final MutableLiveData<ResponseBody> mutableLiveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApi().addFavorite(favorite).enqueue(new Callback<ResponseBody>() {
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

    public LiveData<ResponseBody> removeFavorite(int userId, int productId) {
        final MutableLiveData<ResponseBody> mutableLiveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApi().removeFavorite(userId, productId).enqueue(new Callback<ResponseBody>() {
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
}
package com.dardev.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dardev.model.FavoriteApiResponse;
import com.dardev.net.RetrofitClient;

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
}
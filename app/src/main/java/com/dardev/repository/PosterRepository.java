package com.dardev.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dardev.model.NewsFeedResponse;
import com.dardev.net.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PosterRepository {
    private static final String TAG = PosterRepository.class.getSimpleName();
    private Application application;

    public PosterRepository(Application application) {
        this.application = application;
    }

    public LiveData<NewsFeedResponse> getPosters() {
        final MutableLiveData<NewsFeedResponse> mutableLiveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApi().getPosters().enqueue(new Callback<NewsFeedResponse>() {
            @Override
            public void onResponse(Call<NewsFeedResponse> call, Response<NewsFeedResponse> response) {
                Log.d(TAG, "onResponse: Succeeded");

                NewsFeedResponse newsFeedResponse = response.body();

                if (response.body() != null) {
                    mutableLiveData.setValue(newsFeedResponse);
                }
            }

            @Override
            public void onFailure(Call<NewsFeedResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

        return mutableLiveData;
    }
}
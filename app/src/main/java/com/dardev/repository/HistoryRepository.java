package com.dardev.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dardev.model.History;
import com.dardev.model.HistoryApiResponse;
import com.dardev.net.RetrofitClient;
import com.dardev.utils.RequestCallback;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryRepository {
    private static final String TAG = HistoryRepository.class.getSimpleName();
    private Application application;

    public HistoryRepository(Application application) {
        this.application = application;
    }

    public LiveData<ResponseBody> addToHistory(History history) {
        final MutableLiveData<ResponseBody> mutableLiveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApi().addToHistory(history).enqueue(new Callback<ResponseBody>() {
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

    public LiveData<ResponseBody> removeAllFromHistory() {
        final MutableLiveData<ResponseBody> mutableLiveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApi().removeAllFromHistory().enqueue(new Callback<ResponseBody>() {
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

    public LiveData<HistoryApiResponse> getProductsInHistory(int userId, int page) {
        final MutableLiveData<HistoryApiResponse> mutableLiveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApi().getProductsInHistory(userId, page).enqueue(new Callback<HistoryApiResponse>() {
            @Override
            public void onResponse(Call<HistoryApiResponse> call, Response<HistoryApiResponse> response) {
                Log.d(TAG, "onResponse: Succeeded");

                HistoryApiResponse historyApiResponse = response.body();

                if (response.body() != null) {
                    mutableLiveData.setValue(historyApiResponse);
                }
            }

            @Override
            public void onFailure(Call<HistoryApiResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

        return mutableLiveData;
    }
}
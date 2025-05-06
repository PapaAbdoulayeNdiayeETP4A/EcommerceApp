package com.dardev.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dardev.model.FavoriteApiResponse;
import com.dardev.repository.FavoriteRepository;

public class FavoriteViewModel extends AndroidViewModel {
    private static final String TAG = "FavoriteViewModel";

    private FavoriteRepository favoriteRepository;

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        favoriteRepository = new FavoriteRepository(application);
    }

    public LiveData<FavoriteApiResponse> getFavorites(int userId) {
        Log.d(TAG, "Getting favorites for user: " + userId);
        return favoriteRepository.getFavorites(userId);
    }
}
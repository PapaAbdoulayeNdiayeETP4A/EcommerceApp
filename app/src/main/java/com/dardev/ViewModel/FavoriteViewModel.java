package com.dardev.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dardev.model.Favorite;
import com.dardev.model.FavoriteApiResponse;
import com.dardev.repository.FavoriteRepository;

import okhttp3.ResponseBody;

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

    public LiveData<ResponseBody> addFavorite(Favorite favorite) {
        Log.d(TAG, "Adding to favorites: " + favorite.getProductId());
        return favoriteRepository.addFavorite(favorite);
    }

    public LiveData<ResponseBody> removeFavorite(int userId, int productId) {
        Log.d(TAG, "Removing from favorites: " + productId);
        return favoriteRepository.removeFavorite(userId, productId);
    }
}
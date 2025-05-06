package com.dardev.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dardev.model.NewsFeedResponse;
import com.dardev.repository.PosterRepository;

public class PosterViewModel extends AndroidViewModel {
    private static final String TAG = "PosterViewModel";

    private PosterRepository posterRepository;

    public PosterViewModel(@NonNull Application application) {
        super(application);
        posterRepository = new PosterRepository(application);
    }

    public LiveData<NewsFeedResponse> getPosters() {
        Log.d(TAG, "Getting promotional posters");
        return posterRepository.getPosters();
    }
}
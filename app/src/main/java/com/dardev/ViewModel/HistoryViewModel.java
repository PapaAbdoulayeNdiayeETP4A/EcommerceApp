package com.dardev.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dardev.model.History;
import com.dardev.model.HistoryApiResponse;
import com.dardev.repository.HistoryRepository;
import com.dardev.utils.RequestCallback;

import okhttp3.ResponseBody;

public class HistoryViewModel extends AndroidViewModel {
    private static final String TAG = "HistoryViewModel";

    private HistoryRepository historyRepository;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        historyRepository = new HistoryRepository(application);
    }

    public LiveData<ResponseBody> addToHistory(History history) {
        Log.d(TAG, "Adding product to history");
        return historyRepository.addToHistory(history);
    }

    public LiveData<ResponseBody> removeAllFromHistory() {
        Log.d(TAG, "Removing all items from history");
        return historyRepository.removeAllFromHistory();
    }

    public LiveData<HistoryApiResponse> getProductsInHistory(int userId, int page) {
        Log.d(TAG, "Getting history for user: " + userId + ", page: " + page);
        return historyRepository.getProductsInHistory(userId, page);
    }
}
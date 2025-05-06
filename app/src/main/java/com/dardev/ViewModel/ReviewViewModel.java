package com.dardev.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dardev.model.Review;
import com.dardev.model.ReviewApiResponse;
import com.dardev.repository.ReviewRepository;

import okhttp3.ResponseBody;

public class ReviewViewModel extends AndroidViewModel {
    private static final String TAG = "ReviewViewModel";

    private ReviewRepository reviewRepository;

    public ReviewViewModel(@NonNull Application application) {
        super(application);
        reviewRepository = new ReviewRepository(application);
    }

    public LiveData<ResponseBody> addReview(Review review) {
        Log.d(TAG, "Adding review for product: " + review.getProductId());
        return reviewRepository.addReview(review);
    }

    public LiveData<ReviewApiResponse> getAllReviews(int productId) {
        Log.d(TAG, "Getting reviews for product: " + productId);
        return reviewRepository.getAllReviews(productId);
    }
}
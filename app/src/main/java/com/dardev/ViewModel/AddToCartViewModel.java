package com.dardev.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dardev.model.Cart;
import com.dardev.repository.AddToCartRepository;
import com.dardev.utils.RequestCallback;

import okhttp3.ResponseBody;

public class AddToCartViewModel extends AndroidViewModel {
    private static final String TAG = "AddToCartViewModel";

    private AddToCartRepository addToCartRepository;

    public AddToCartViewModel(@NonNull Application application) {
        super(application);
        addToCartRepository = new AddToCartRepository(application);
    }

    public LiveData<ResponseBody> addToCart(Cart cart, RequestCallback callback) {
        Log.d(TAG, "Adding product to cart");
        return addToCartRepository.addToCart(cart, callback);
    }
}
package com.dardev.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dardev.model.OrderApiResponse;
import com.dardev.model.Ordering;
import com.dardev.model.Shipping;
import com.dardev.repository.OrderRepository;

import okhttp3.ResponseBody;

public class OrderViewModel extends AndroidViewModel {
    private static final String TAG = "OrderViewModel";

    private OrderRepository orderRepository;

    public OrderViewModel(@NonNull Application application) {
        super(application);
        orderRepository = new OrderRepository(application);
    }

    public LiveData<OrderApiResponse> getOrders(int userId) {
        Log.d(TAG, "Getting orders for user: " + userId);
        return orderRepository.getOrders(userId);
    }

    public LiveData<ResponseBody> addShippingAddress(Shipping shipping) {
        Log.d(TAG, "Adding shipping address for user: " + shipping.getUserId());
        return orderRepository.addShippingAddress(shipping);
    }

    public LiveData<ResponseBody> orderProduct(Ordering ordering) {
        Log.d(TAG, "Creating order for user: " + ordering.getUserId());
        return orderRepository.orderProduct(ordering);
    }
}
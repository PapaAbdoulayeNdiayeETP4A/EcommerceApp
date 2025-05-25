package com.dardev.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dardev.model.Order;
import com.dardev.model.OrderApiResponse;
import com.dardev.model.Ordering;
import com.dardev.repository.OrderRepository;

import okhttp3.ResponseBody; // Importez ResponseBody
import java.util.List;

public class OrderViewModel extends AndroidViewModel {

    private OrderRepository orderRepository;
    private static final String TAG = "OrderViewModel";

    public OrderViewModel(@NonNull Application application) {
        super(application);
        orderRepository = new OrderRepository(application);
    }

    // Récupérer les commandes d'un utilisateur
    public LiveData<List<Order>> getOrders(int userId) {
        MutableLiveData<List<Order>> ordersLiveData = new MutableLiveData<>();
        orderRepository.getOrders(userId).observeForever(response -> {
            if (response != null && response.getOrders() != null) {
                ordersLiveData.setValue(response.getOrders());
            } else {
                ordersLiveData.setValue(null); // Ou une liste vide
                Log.e(TAG, "Failed to fetch orders or response is null.");
            }
        });
        return ordersLiveData;
    }

    // Placer une nouvelle commande
    public LiveData<ResponseBody> placeOrder(Ordering ordering) {
        MutableLiveData<ResponseBody> responseLiveData = new MutableLiveData<>();
        orderRepository.orderProduct(ordering).observeForever(responseBody -> {
            if (responseBody != null) {
                responseLiveData.setValue(responseBody);
            } else {
                responseLiveData.setValue(null);
                Log.e(TAG, "Failed to place order or response is null.");
            }
        });
        return responseLiveData;
    }

    // Méthode pour obtenir les détails d'une commande spécifique (utilisé par OrderDetailsActivity)
    public LiveData<Order> getOrderDetails(int orderId, int userId) {
        MutableLiveData<Order> orderDetailsLiveData = new MutableLiveData<>();
        getOrders(userId).observeForever(orders -> {
            if (orders != null) {
                for (Order order : orders) {
                    if (order.getId() == orderId) {
                        orderDetailsLiveData.setValue(order);
                        break;
                    }
                }
                if (orderDetailsLiveData.getValue() == null) {
                    Log.e(TAG, "Order with ID " + orderId + " not found for user " + userId);
                }
            } else {
                Log.e(TAG, "No orders found for user " + userId);
            }
        });
        return orderDetailsLiveData;
    }
}
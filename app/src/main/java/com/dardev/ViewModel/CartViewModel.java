package com.dardev.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dardev.model.Cart;
import com.dardev.model.CartApiResponse;
import com.dardev.repository.CartRepository;
import okhttp3.ResponseBody;

public class CartViewModel extends AndroidViewModel
{
    private CartRepository cartRepository;

    public CartViewModel(@NonNull Application application)
    {
        super(application);
        cartRepository = new CartRepository(application);
    }

    public LiveData<CartApiResponse> getProductsInCart(int userId)
    {
        return cartRepository.getProductsInCart(userId);
    }

    public LiveData<ResponseBody> addToCart(Cart cart) {
        return cartRepository.addToCart(cart);
    }

    /**
     * Supprimer un produit du panier
     */
    public LiveData<ResponseBody> removeFromCart(int userId, int productId) {
        return cartRepository.removeFromCart(userId, productId);
    }

    /**
     * Vider complètement le panier
     */
//    public LiveData<ResponseBody> clearCart(int userId) {
//        return cartRepository.clearCart(userId);
//    }
}
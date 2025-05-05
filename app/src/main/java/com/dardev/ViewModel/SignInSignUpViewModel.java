package com.dardev.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dardev.model.CartApiResponse;
import com.dardev.repository.CartRepository;
import com.dardev.repository.SignInSignUpRepository;

public class SignInSignUpViewModel extends AndroidViewModel
{
    private SignInSignUpRepository signInSignUpRepository;

    public SignInSignUpViewModel(@NonNull Application application)
    {
        super(application);
        signInSignUpRepository = new SignInSignUpRepository(application);
    }

   /* public LiveData<CartApiResponse> getProductsInCart(int userId)
    {
        return signInSignUpRepository.getProductsInCart(userId);
    }*/
}

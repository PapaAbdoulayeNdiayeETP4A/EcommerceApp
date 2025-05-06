package com.dardev.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dardev.model.LoginApiResponse;
import com.dardev.model.Otp;
import com.dardev.model.RegisterApiResponse;
import com.dardev.model.User;
import com.dardev.repository.SignInSignUpRepository;

public class SignInSignUpViewModel extends AndroidViewModel {
    private static final String TAG = "SignInSignUpViewModel";

    private SignInSignUpRepository signInSignUpRepository;
    private MutableLiveData<LoginApiResponse> loginResponseLiveData = new MutableLiveData<>();
    private MutableLiveData<RegisterApiResponse> registerResponseLiveData = new MutableLiveData<>();
    private MutableLiveData<Otp> otpResponseLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public SignInSignUpViewModel(@NonNull Application application) {
        super(application);
        signInSignUpRepository = new SignInSignUpRepository(application);
    }

    public LiveData<LoginApiResponse> getLoginResponseLiveData() {
        return loginResponseLiveData;
    }

    public LiveData<RegisterApiResponse> getRegisterResponseLiveData() {
        return registerResponseLiveData;
    }

    public LiveData<Otp> getOtpResponseLiveData() {
        return otpResponseLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loginUser(String email, String password) {
        Log.d(TAG, "Attempting to login user with email: " + email);
        isLoading.setValue(true);

        LiveData<LoginApiResponse> response = signInSignUpRepository.loginUser(email, password);
        response.observeForever(loginResponse -> {
            loginResponseLiveData.setValue(loginResponse);
            isLoading.setValue(false);
        });
    }

    public void registerUser(User user) {
        Log.d(TAG, "Attempting to register user with email: " + user.getEmail());
        isLoading.setValue(true);

        LiveData<RegisterApiResponse> response = signInSignUpRepository.registerUser(user);
        response.observeForever(registerResponse -> {
            registerResponseLiveData.setValue(registerResponse);
            isLoading.setValue(false);
        });
    }

    public void getOtp(String email) {
        Log.d(TAG, "Requesting OTP for email: " + email);
        isLoading.setValue(true);

        LiveData<Otp> response = signInSignUpRepository.getOtp(email);
        response.observeForever(otp -> {
            otpResponseLiveData.setValue(otp);
            isLoading.setValue(false);
        });
    }
}
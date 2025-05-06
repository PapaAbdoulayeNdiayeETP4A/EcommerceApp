package com.dardev.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dardev.model.LoginApiResponse;
import com.dardev.model.RegisterApiResponse;
import com.dardev.model.User;
import com.dardev.model.Otp;
import com.dardev.net.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInSignUpRepository {
    private static final String TAG = "SignInSignUpRepository";
    private Application application;

    public SignInSignUpRepository(Application application) {
        this.application = application;
    }

    public LiveData<RegisterApiResponse> registerUser(User user) {
        final MutableLiveData<RegisterApiResponse> mutableLiveData = new MutableLiveData<>();

        try {
            Log.d(TAG, "Attempting to register user: " + user.getEmail());
            RetrofitClient.getInstance().getApi().createUser(user).enqueue(new Callback<RegisterApiResponse>() {
                @Override
                public void onResponse(Call<RegisterApiResponse> call, Response<RegisterApiResponse> response) {
                    Log.d(TAG, "Register onResponse: " + response.code());

                    if (response.isSuccessful()) {
                        RegisterApiResponse registerResponse = response.body();
                        Log.d(TAG, "Register successful: " + (registerResponse != null));
                        mutableLiveData.setValue(registerResponse);
                    } else {
                        Log.e(TAG, "Register error: " + response.code());
                        // Create error response
                        RegisterApiResponse errorResponse = new RegisterApiResponse();
                        errorResponse.setSuccess(false);
                        errorResponse.setMessage("Registration failed: " + response.message());
                        mutableLiveData.setValue(errorResponse);
                    }
                }

                @Override
                public void onFailure(Call<RegisterApiResponse> call, Throwable t) {
                    Log.e(TAG, "Register onFailure: " + t.getMessage(), t);
                    // Create error response
                    RegisterApiResponse errorResponse = new RegisterApiResponse();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Network error: " + t.getMessage());
                    mutableLiveData.setValue(errorResponse);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in registerUser", e);
            // Create error response
            RegisterApiResponse errorResponse = new RegisterApiResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Error: " + e.getMessage());
            mutableLiveData.setValue(errorResponse);
        }

        return mutableLiveData;
    }

    public LiveData<LoginApiResponse> loginUser(String email, String password) {
        final MutableLiveData<LoginApiResponse> mutableLiveData = new MutableLiveData<>();

        try {
            Log.d(TAG, "Attempting to login user: " + email);
            RetrofitClient.getInstance().getApi().loginUser(email, password).enqueue(new Callback<LoginApiResponse>() {
                @Override
                public void onResponse(Call<LoginApiResponse> call, Response<LoginApiResponse> response) {
                    Log.d(TAG, "Login onResponse: " + response.code());

                    if (response.isSuccessful()) {
                        LoginApiResponse loginResponse = response.body();
                        Log.d(TAG, "Login successful: " + (loginResponse != null));
                        mutableLiveData.setValue(loginResponse);
                    } else {
                        Log.e(TAG, "Login error: " + response.code());
                        // Create error response
                        LoginApiResponse errorResponse = new LoginApiResponse();
                        errorResponse.setSuccess(false);
                        errorResponse.setMessage("Login failed: " + response.message());
                        mutableLiveData.setValue(errorResponse);
                    }
                }

                @Override
                public void onFailure(Call<LoginApiResponse> call, Throwable t) {
                    Log.e(TAG, "Login onFailure: " + t.getMessage(), t);
                    // Create error response
                    LoginApiResponse errorResponse = new LoginApiResponse();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Network error: " + t.getMessage());
                    mutableLiveData.setValue(errorResponse);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in loginUser", e);
            // Create error response
            LoginApiResponse errorResponse = new LoginApiResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Error: " + e.getMessage());
            mutableLiveData.setValue(errorResponse);
        }

        return mutableLiveData;
    }

    public LiveData<Otp> getOtp(String email) {
        final MutableLiveData<Otp> mutableLiveData = new MutableLiveData<>();

        try {
            Log.d(TAG, "Requesting OTP for: " + email);
            RetrofitClient.getInstance().getApi().getOtp(email).enqueue(new Callback<Otp>() {
                @Override
                public void onResponse(Call<Otp> call, Response<Otp> response) {
                    Log.d(TAG, "OTP onResponse: " + response.code());

                    if (response.isSuccessful()) {
                        Otp otp = response.body();
                        Log.d(TAG, "OTP received: " + (otp != null));
                        mutableLiveData.setValue(otp);
                    } else {
                        Log.e(TAG, "OTP error: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Otp> call, Throwable t) {
                    Log.e(TAG, "OTP onFailure: " + t.getMessage(), t);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception in getOtp", e);
        }

        return mutableLiveData;
    }
}
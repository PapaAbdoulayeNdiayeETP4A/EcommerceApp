package com.dardev.ViewModel;

import android.app.Application;
import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dardev.model.User;
import com.dardev.net.RetrofitClient;
import com.dardev.repository.UserRepository;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewModel extends AndroidViewModel {
    private static final String TAG = "UserViewModel";

    private UserRepository userRepository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public LiveData<ResponseBody> deleteAccount(int userId) {
        Log.d(TAG, "Deleting account for user: " + userId);
        return userRepository.deleteAccount(userId);
    }

    public LiveData<ResponseBody> uploadPhoto(MultipartBody.Part userPhoto, RequestBody userId) {
        Log.d(TAG, "Uploading user photo");
        return userRepository.uploadPhoto(userPhoto, userId);
    }

    public LiveData<ResponseBody> updatePassword(String password, int userId) {
        Log.d(TAG, "Updating password for user: " + userId);
        return userRepository.updatePassword(password, userId);
    }

    public LiveData<Image> getUserImage(int userId) {
        Log.d(TAG, "Getting image for user: " + userId);
        return userRepository.getUserImage(userId);
    }

    public LiveData<User> getUserDetails(int userId) {
        Log.d(TAG, "Getting details for user: " + userId);
        return userRepository.getUserDetails(userId);
    }

    public LiveData<ResponseBody> updateProfile(String username, String email, int userId) {
        final MutableLiveData<ResponseBody> mutableLiveData = new MutableLiveData<>();

        RetrofitClient.getInstance().getApi().updateProfile(username, email, userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "updateProfile onResponse: " + response.code());
                if (response.isSuccessful()) {
                    mutableLiveData.setValue(response.body());
                } else {
                    mutableLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "updateProfile onFailure: " + t.getMessage());
                mutableLiveData.setValue(null);
            }
        });
        return mutableLiveData;
    }
}
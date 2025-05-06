package com.dardev.net;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // Pour les tests sur l'émulateur Android
    private static final String BASE_URL = "http://10.0.2.2:8000/";

    // Pour les tests sur un appareil physique (remplacez par votre adresse IP locale)
    // private static final String BASE_URL = "http://192.168.1.X:8000/";

    // Pour la production (remplacez par votre URL de serveur)
    // private static final String BASE_URL = "https://votre-serveur.com/";

    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private RetrofitClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (mInstance == null)
            mInstance = new RetrofitClient();
        return mInstance;
    }

    public Api getApi() {
        return retrofit.create(Api.class);
    }
}
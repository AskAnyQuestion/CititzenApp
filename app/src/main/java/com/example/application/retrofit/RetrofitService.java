package com.example.application.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private Retrofit retrofit;

    public RetrofitService() {
        initializerRetrofit();
    }

    public void initializerRetrofit() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

         retrofit = new Retrofit.Builder() // 192.168.43.247 - PC
                 .baseUrl("http://192.168.0.104:8000")
                 .addConverterFactory(GsonConverterFactory.create(gson))
                 .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}

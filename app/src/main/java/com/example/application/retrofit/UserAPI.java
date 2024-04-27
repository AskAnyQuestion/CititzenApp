package com.example.application.retrofit;

import com.example.application.data.LoginData;
import com.example.application.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserAPI {
    @POST("/registration")
    Call<Integer> registration(@Body User user);

    @POST("/authorization")
    Call<Integer> authorization(@Body LoginData loginData);
}

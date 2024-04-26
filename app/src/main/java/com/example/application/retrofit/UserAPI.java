package com.example.application.retrofit;

import com.example.application.model.User;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserAPI {
    @POST("/registration")
    void registration(@Body User user);

    @POST("/authorization")
    void authorization(Long phone, String password);
}

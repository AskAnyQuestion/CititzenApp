package com.example.application.retrofit;

import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface NotificationAPI {
    @Multipart
    @POST("/notification")
    Call<Integer> add(@Part("userId") String userId, @Part("notificationId") String notificationId);
}

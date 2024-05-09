package com.example.application.retrofit;

import com.example.application.data.NotificationData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface NotificationAPI {
    @POST("/notification/add")
    Call<Integer> add(@Body NotificationData notificationData);
}

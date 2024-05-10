package com.example.application.retrofit;

import com.example.application.data.LoginData;
import com.example.application.data.UserData;
import com.example.application.model.Incident;
import com.example.application.model.Notification;
import com.example.application.model.User;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface UserAPI {
    @POST("/user/registration")
    Call<Integer> registration(@Body User user);
    @POST("/user/authorization")
    Call<Integer> authorization(@Body LoginData loginData);
    @POST("/user/update")
    Call<Integer> userUpdate(@Body User user);
    @POST("/user/updates")
    Call<Integer> updates(@Part("oldLogin") String oldLogin, @Part("oldPhone") Long oldPhone,
                          @Part("newLogin") String newLogin, @Part("newPhone") Long newPhone);
    @POST("/user/get")
    Call<List<Incident>> get(@Body UserData userData);
    @POST("/user/delete")
    Call<Integer> delete(@Body UserData userData);
}

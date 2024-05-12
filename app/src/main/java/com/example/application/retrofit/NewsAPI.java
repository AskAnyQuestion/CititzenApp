package com.example.application.retrofit;

import com.example.application.model.News;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface NewsAPI {
    @GET("/news/get")
    Call<List<News>> get();
    @GET("/news/all")
    Call<ResponseBody> all();
}

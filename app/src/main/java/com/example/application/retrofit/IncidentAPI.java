package com.example.application.retrofit;

import com.example.application.model.Incident;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.io.File;
import java.util.List;


public interface IncidentAPI {
    @Multipart
    @POST("/incident/add")
    Call<Integer> addIncident(@Part("incident") Incident incident, @Part List<MultipartBody.Part> files);
    @GET("/incident/get")
    Call<List <Incident>> getIncidents();
    @GET("/incident/materials")
    Call<ResponseBody> getMaterials();
    @GET("/incident/material/{id}")
    Call<ResponseBody> getMaterial(@Path("id") String id);

}

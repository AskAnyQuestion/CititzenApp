package com.example.application.retrofit;

import com.example.application.model.Incident;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import java.util.List;


public interface IncidentAPI {
    @Multipart
    @POST("/incident/add")
    Call<Integer> addIncident(@Part("incident") Incident incident, @Part List<MultipartBody.Part> files);
}

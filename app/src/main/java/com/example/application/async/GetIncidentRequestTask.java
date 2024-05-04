package com.example.application.async;

import android.os.AsyncTask;
import com.example.application.model.Incident;
import com.example.application.retrofit.IncidentAPI;
import com.example.application.retrofit.RetrofitService;
import retrofit2.Call;

import java.util.List;

public class GetIncidentRequestTask extends AsyncTask<Void, Void, Call<List <Incident>>> {
    @Override
    protected Call<List <Incident>> doInBackground(Void... voids) {
        RetrofitService retrofitService = new RetrofitService();
        IncidentAPI incidentAPI = retrofitService.getRetrofit().create(IncidentAPI.class);
        return incidentAPI.getIncidents();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Call<List <Incident>> unused) {
        super.onPostExecute(unused);
    }
}

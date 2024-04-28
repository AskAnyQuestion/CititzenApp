package com.example.application.async;

import android.os.AsyncTask;
import com.example.application.retrofit.IncidentAPI;
import com.example.application.retrofit.RetrofitService;
import okhttp3.ResponseBody;
import retrofit2.Call;

import java.util.List;

public class GetIncidentRequestTask extends AsyncTask<Void, Void, Call<List <Object>>> {
    @Override
    protected Call<List <Object>> doInBackground(Void... voids) {
        RetrofitService retrofitService = new RetrofitService();
        IncidentAPI incidentAPI = retrofitService.getRetrofit().create(IncidentAPI.class);
        Call<List<Object>> call = incidentAPI.getIncidents();
        return call;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Call<List <Object>> unused) {
        super.onPostExecute(unused);
    }
}

package com.example.application.async;

import android.os.AsyncTask;
import com.example.application.retrofit.IncidentAPI;
import com.example.application.retrofit.RetrofitService;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class GetMaterialRequestTask extends AsyncTask<Void, Void, Call<ResponseBody>> {
    @Override
    protected Call<ResponseBody> doInBackground(Void... voids) {
        RetrofitService retrofitService = new RetrofitService();
        IncidentAPI incidentAPI = retrofitService.getRetrofit().create(IncidentAPI.class);
        return incidentAPI.getMaterials();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Call<ResponseBody> unused) {
        super.onPostExecute(unused);
    }
}

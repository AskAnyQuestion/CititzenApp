package com.example.application.async;

import android.os.AsyncTask;
import com.example.application.data.UserData;
import com.example.application.retrofit.RetrofitService;
import com.example.application.retrofit.UserAPI;
import retrofit2.Call;

public class ClearIncidentsRequestTask extends AsyncTask<Void, Void, Call<Integer>> {
    private final UserData userData;
    public ClearIncidentsRequestTask(UserData userData) {
        this.userData = userData;
    }
    @Override
    protected Call<Integer> doInBackground(Void... voids) {
        RetrofitService retrofitService = new RetrofitService();
        UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
        userAPI.delete(userData);
        return userAPI.delete(userData);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Call<Integer> unused) {
        super.onPostExecute(unused);
    }
}

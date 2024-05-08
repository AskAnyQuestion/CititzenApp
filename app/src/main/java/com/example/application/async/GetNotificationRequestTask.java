package com.example.application.async;

import android.os.AsyncTask;
import com.example.application.data.UserData;
import com.example.application.model.Notification;
import com.example.application.retrofit.RetrofitService;
import com.example.application.retrofit.UserAPI;
import retrofit2.Call;

import java.util.List;

public class GetNotificationRequestTask extends AsyncTask<Void, Void, Call<List<Notification>>> {
    private final UserData userData;
    public GetNotificationRequestTask(UserData userData) {
        this.userData = userData;
    }
    @Override
    protected Call<List<Notification>> doInBackground(Void... voids) {
        RetrofitService retrofitService = new RetrofitService();
        UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
        return userAPI.get(userData);
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Call<List<Notification>> unused) {
        super.onPostExecute(unused);
    }
}

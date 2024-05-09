package com.example.application.async;

import android.os.AsyncTask;
import com.example.application.data.NotificationData;
import com.example.application.retrofit.NotificationAPI;
import com.example.application.retrofit.RetrofitService;
import retrofit2.Call;

public class AddNotificationRequestTask extends AsyncTask<Void, Void, Call<Integer>> {
    private final NotificationData notificationData;
    public AddNotificationRequestTask(NotificationData notificationData) {
        this.notificationData = notificationData;
    }
    @Override
    protected Call<Integer> doInBackground(Void... voids) {
        RetrofitService retrofitService = new RetrofitService();
        NotificationAPI notificationAPI = retrofitService.getRetrofit().create(NotificationAPI.class);
        return notificationAPI.add(notificationData);
    }
}

package com.example.application.async;

import android.os.AsyncTask;
import com.example.application.model.User;
import com.example.application.retrofit.RetrofitService;
import com.example.application.retrofit.UserAPI;


public class RegistrationRequestTask extends AsyncTask<Void, Void, Void> {
    private final User user;
    public RegistrationRequestTask(User user) {
        this.user = user;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        RetrofitService retrofitService = new RetrofitService();
        UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
        userAPI.registration(user);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
    }
}

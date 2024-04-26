package com.example.application.async;

import android.os.AsyncTask;
import com.example.application.retrofit.RetrofitService;
import com.example.application.retrofit.UserAPI;

public class AuthorizationRequestTask extends AsyncTask<Void, Void, Void> {
    private final Long phone;
    private final String password;
    public AuthorizationRequestTask(Long phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        RetrofitService retrofitService = new RetrofitService();
        UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
        userAPI.authorization(phone, password);
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